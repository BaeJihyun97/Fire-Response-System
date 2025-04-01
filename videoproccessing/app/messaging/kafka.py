import json
import asyncio
from typing import Callable, Dict, Type
from confluent_kafka import Consumer, Producer, KafkaException
from pydantic import BaseModel
from app.core.config import get_settings
from app.schemas.events import (
    VideoStoredEvent,
    VideoAnalysisCompletedEvent,
    VideoAnalysisRequestedEvent,
    VideoBlurCompletedEvent,
    TOPICS,
)
from app.services.video_indexer import VideoIndexerClient
from app.models.video import Video


class KafkaProducer:
    def __init__(self):
        self.settings = get_settings()
        connection_string = self.settings.azure_event_hub_connection_string
        # Parse connection string to get required values
        config = self._parse_connection_string(connection_string)

        self.producer = Producer(
            {
                "bootstrap.servers": f"{config['namespace']}.servicebus.windows.net:9093",
                "security.protocol": "SASL_SSL",
                "sasl.mechanism": "PLAIN",
                "sasl.username": "$ConnectionString",
                "sasl.password": connection_string,
                "client.id": "video-processing-producer",
            }
        )

    def _parse_connection_string(self, connection_string: str) -> dict:
        """Parse Event Hub connection string to get namespace and other details"""
        parts = dict(part.split("=", 1) for part in connection_string.split(";"))
        return {
            "namespace": parts["Endpoint"].split(".")[0].split("//")[1],
            "shared_access_key_name": parts.get("SharedAccessKeyName"),
            "shared_access_key": parts.get("SharedAccessKey"),
        }

    def publish(self, event_type: str, event: BaseModel):
        topic = "fireresponsesystem"
        try:
            self.producer.produce(
                topic,
                key=None,
                value=event.model_dump_json(by_alias=True).encode("utf-8"),
                headers=[
                    ("type", event_type.encode("utf-8")),
                    ("contentType", "application/json".encode("utf-8")),
                ],
                callback=self._delivery_callback,
            )
            self.producer.poll(0)
        except KafkaException as e:
            print(f"Failed to publish message to {topic}: {str(e)}")
            raise

    def _delivery_callback(self, err, msg):
        if err:
            print(f"Message delivery failed: {str(err)}")


class KafkaConsumer:
    def __init__(self):
        self.settings = get_settings()
        connection_string = self.settings.azure_event_hub_connection_string
        # Parse connection string to get required values
        config = self._parse_connection_string(connection_string)

        self.consumer = Consumer(
            {
                "bootstrap.servers": f"{config['namespace']}.servicebus.windows.net:9093",
                "security.protocol": "SASL_SSL",
                "sasl.mechanism": "PLAIN",
                "sasl.username": "$ConnectionString",
                "sasl.password": connection_string,
                "group.id": "video-processing-group",
                "auto.offset.reset": "earliest",
                "enable.auto.commit": True,
            }
        )

        self.producer = KafkaProducer()
        self.event_handlers: Dict[str, list[Callable]] = {
            "EventVideoIdUpdated": [
                self._handle_analysis_requested,
                self._handle_blur_face_requested,
            ],
        }
        self.running = False

    def _parse_connection_string(self, connection_string: str) -> dict:
        """Parse Event Hub connection string to get namespace and other details"""
        parts = dict(part.split("=", 1) for part in connection_string.split(";"))
        return {
            "namespace": parts["Endpoint"].split(".")[0].split("//")[1],
            "shared_access_key_name": parts.get("SharedAccessKeyName"),
            "shared_access_key": parts.get("SharedAccessKey"),
        }

    async def start_consuming(self):
        """Start consuming messages from Kafka topics."""
        print("Starting Kafka consumer...")
        self.running = True
        self.consumer.subscribe(["fireresponsesystem"])

        while self.running:
            try:
                # Poll for messages
                msg = self.consumer.poll(1.0)

                if msg is None:
                    await asyncio.sleep(0.1)
                    continue

                if msg.error():
                    print(f"Consumer error: {msg.error()}")
                    continue

                try:
                    # Parse the message
                    event_data = json.loads(msg.value().decode("utf-8"))
                    # Get the appropriate event type based on the headers
                    headers_dict = dict(msg.headers() or {})
                    target_type = headers_dict.get("type", "")
                    if target_type:
                        try:
                            target_type = target_type.decode("utf-8")
                        except:
                            target_type = "EventVideoIdUpdated"

                    event_type = self._get_event_type(target_type)
                    if not event_type:
                        print(f"This event type is not configured: {target_type}")
                        continue

                    # Get the event handler for this topic
                    handlers = self.event_handlers.get(target_type, list())
                    if not handlers:
                        print(f"No handler found for the event type: {target_type}")
                        continue
                    print(f"Parsed event data for {target_type}: {event_data}")
                    event = event_type(**event_data)

                    # Process the event
                    await asyncio.gather(*(handler(event) for handler in handlers))

                except Exception as e:
                    print(f"Error processing message: {str(e)}")

            except Exception as e:
                print(f"Error in consumer loop: {str(e)}")
                await asyncio.sleep(1)  # Wait before retrying

    def stop(self):
        """Stop the consumer."""
        print("Stopping Kafka consumer...")
        self.running = False
        self.consumer.close()
        print("Kafka consumer stopped")

    def _get_event_type(self, topic: str) -> Type[BaseModel]:
        """Get the appropriate event type based on the topic."""
        event_types = {
            TOPICS["video_stored"]: VideoStoredEvent,
            "EventVideoIdUpdated": VideoAnalysisRequestedEvent,
            TOPICS["video_analysis_completed"]: VideoAnalysisCompletedEvent,
        }
        return event_types.get(topic)

    async def _handle_analysis_requested(self, event: VideoAnalysisRequestedEvent):
        """Handle video analysis requested event."""
        try:
            print(f"Processing analysis requested event for video: {event.video_id}")
            from app.services.policy_handler import PolicyHandler

            policy_handler = PolicyHandler()

            # Process the event using policy handler
            result = await policy_handler.handle_event(
                "video_analysis_requested", event.model_dump()
            )
            print(f"Policy handler result: {result}")

            if result["success"]:
                if result["fire_detected"]:
                    # Publish analysis completed event
                    completed_event = VideoAnalysisCompletedEvent(
                        video_analysis_id=result["video_analysis_id"],
                        video_id=event.video_id,
                        report_id=event.report_id,
                        event_id=event.event_id,
                        success=result["success"],
                        fire_detected=result["fire_detected"],
                    )
                    self.producer.publish("VideoAnalyzed", completed_event)
            else:
                # Publish failed event
                failed_event = VideoAnalysisCompletedEvent(
                    video_analysis_id=None,
                    video_id=event.video_id,
                    report_id=event.report_id,
                    event_id=event.event_id,
                    success=False,
                    fire_detected=False,
                )
                self.producer.publish("VideoAnalysisFailed", failed_event)

        except Exception as e:
            print(f"Error processing analysis requested event: {str(e)}")
            failed_event = VideoAnalysisCompletedEvent(
                video_analysis_id=None,
                video_id=event.video_id,
                report_id=event.report_id,
                event_id=event.event_id,
                success=False,
                fire_detected=False,
            )
            self.producer.publish("VideoAnalysisFailed", failed_event)

    async def _handle_blur_face_requested(self, event: VideoAnalysisRequestedEvent):
        """Handle video analysis requested event."""
        # Placeholder logic
        video = await Video.find_one({"video_id": event.video_id})
        if video:
            completed_event = VideoBlurCompletedEvent(
                event_id=event.event_id,
                blurred_video_uri=video.processed_video_uri,
            )
            self.producer.publish("FaceBlurred", completed_event)
        # try:
        #     video_indexer = self._get_video_indexer_client()

        #     # Get the original video URL and parse it for the path
        #     video = await Video.find_one({"video_id": event.video_id})
        #     video_url = video.original_video_uri

        #     # Extract the original path and add '_blurred' before the extension
        #     # Example: if path is 'folder/video.mp4' -> 'folder/video_blurred.mp4'
        #     original_path = video_url.split("?")[
        #         0
        #     ]  # Remove any SAS token or query params
        #     path_without_ext = original_path.rsplit(".", 1)[0]
        #     extension = original_path.rsplit(".", 1)[1]
        #     output_path = f"{path_without_ext}_blurred.{extension}"

        #     # Upload video to Video Indexer using URL
        #     video_id = await video_indexer.upload_video(
        #         video_url=video_url,
        #         privacy_mode="Private",
        #         video_name=f"{event.video_id}.mp4",
        #     )

        #     # Wait for video processing to complete
        #     await video_indexer.wait_for_index_processing(video_id)
        #     print("Video processing completed")

        #     # Get video with face blurring and save to same location
        #     job_id = await video_indexer.blur_faces(
        #         video_id, video_name=f"{event.video_id}_blurred"
        #     )
        #     print(f"Face blurring job started: {job_id}")

        #     await video_indexer.wait_for_job(job_id)

        #     print("Face blurring completed")

        #     # Clean up the video from Video Indexer
        #     # await video_indexer.delete_video(video_id)
        #     blurred_video_id = await video_indexer.get_video_id_by_external_id(video_id)
        #     print(f"Blurred video ID: {blurred_video_id}")

        #     completed_event = VideoBlurCompletedEvent(
        #         video_analysis_id=1,
        #         video_id=event.video_id,
        #         report_id=event.report_id,
        #         event_id=event.event_id,
        #         success=True,
        #         blurred_video_uri="test",
        #     )
        #     self.producer.publish("FaceBlurred", completed_event)

        # except Exception as e:
        #     print(f"Failed to blur faces in video: {str(e)}")

    # async def _handle_analysis_completed(self, event: VideoAnalysisCompletedEvent):
    #     """Handle video analysis completed event."""
    #     try:
    #         print(f"Processing analysis completed event for video: {event.video_id}")
    #         # Import PolicyHandler here to avoid circular import
    #         from app.services.policy_handler import PolicyHandler

    #         policy_handler = PolicyHandler()

    #         # Process the event using policy handler
    #         result = await policy_handler.handle_event(
    #             "video_analysis_completed", event.model_dump()
    #         )
    #         print(f"Policy handler result: {result}")

    #         if result["success"]:
    #             print(f"Analysis completed successfully for video {result['video_id']}")
    #         else:
    #             print(
    #                 f"Analysis failed for video {result['video_id']}: {result.get('error')}"
    #             )

    #     except Exception as e:
    #         print(f"Error processing analysis completed event: {str(e)}")

    def _get_video_indexer_client(self) -> VideoIndexerClient:
        """Get an instance of the Video Indexer client."""
        return VideoIndexerClient(
            account_id=self.settings.azure_video_indexer_account_id,
            account_name=self.settings.azure_video_indexer_account_name,
            resource_group=self.settings.azure_video_indexer_resource_group,
            location=self.settings.azure_video_indexer_location,
            subscription_id=self.settings.azure_video_indexer_subscription_id,
            tenant_id=self.settings.azure_tenant_id,
            client_id=self.settings.azure_client_id,
            client_secret=self.settings.azure_client_secret,
        )
