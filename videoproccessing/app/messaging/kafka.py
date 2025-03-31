import json
import asyncio
from typing import Callable, Dict, Type
from confluent_kafka import Consumer, Producer, KafkaException
from pydantic import BaseModel
from app.schemas.events import (
    VideoStoredEvent,
    VideoAnalysisCompletedEvent,
    VideoAnalysisRequestedEvent,
    TOPICS,
)


class KafkaProducer:
    def __init__(self, bootstrap_servers: str):
        self.producer = Producer(
            {
                "bootstrap.servers": bootstrap_servers,
                "client.id": "video-processing-producer",
            }
        )

    def publish(self, topic: str, event: BaseModel):
        try:
            self.producer.produce(
                topic,
                key=None,
                value=event.model_dump_json().encode("utf-8"),
                callback=self._delivery_callback,
            )
            self.producer.poll(0)
        except KafkaException as e:
            print(f"Failed to publish message to {topic}: {str(e)}")
            raise

    def _delivery_callback(self, err, msg):
        if err:
            print(f"Message delivery failed: {str(err)}")
        else:
            print(f"Message delivered to {msg.topic()} [{msg.partition()}]")


class KafkaConsumer:
    def __init__(self, bootstrap_servers: str):
        self.consumer = Consumer(
            {
                "bootstrap.servers": bootstrap_servers,
                "group.id": "video-processing-group",
                "auto.offset.reset": "earliest",
                "enable.auto.commit": True,
            }
        )
        self.producer = KafkaProducer(bootstrap_servers)
        self.event_handlers: Dict[str, Callable] = {
            TOPICS["video_stored"]: self._handle_video_stored,
            TOPICS["video_analysis_requested"]: self._handle_analysis_requested,
        }
        self.running = False

    async def start_consuming(self):
        """Start consuming messages from Kafka topics."""
        print("Starting Kafka consumer...")
        self.running = True
        self.consumer.subscribe(list(self.event_handlers.keys()))

        while self.running:
            try:
                # Poll for messages
                msg = self.consumer.poll(1.0)

                if msg is None:
                    await asyncio.sleep(0.1)  # Small delay to prevent CPU spinning
                    continue

                if msg.error():
                    print(f"Consumer error: {msg.error()}")
                    continue

                try:
                    print(f"Received message from topic: {msg.topic()}")
                    # Get the event handler for this topic
                    handler = self.event_handlers.get(msg.topic())
                    if not handler:
                        print(f"No handler found for topic: {msg.topic()}")
                        continue

                    # Parse the message
                    event_data = json.loads(msg.value().decode("utf-8"))
                    print(f"Parsed event data: {event_data}")

                    # Get the appropriate event type based on the topic
                    event_type = self._get_event_type(msg.topic())
                    event = event_type(**event_data)

                    # Process the event
                    await handler(event)

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
            TOPICS["video_analysis_requested"]: VideoAnalysisRequestedEvent,
            TOPICS["video_analysis_completed"]: VideoAnalysisCompletedEvent,
        }
        return event_types.get(topic)

    async def _handle_video_stored(self, event: VideoStoredEvent):
        """Handle video stored event."""
        pass

    async def _handle_analysis_requested(self, event: VideoAnalysisRequestedEvent):
        """Handle video analysis requested event."""
        try:
            print(f"Processing analysis requested event for video: {event.video_id}")
            # Import PolicyHandler here to avoid circular import
            from app.services.policy_handler import PolicyHandler

            policy_handler = PolicyHandler()

            # Process the event using policy handler
            result = await policy_handler.handle_event(
                "video_analysis_requested", event.model_dump()
            )

            if result["success"]:
                print(result)
                # Publish analysis completed event
                completed_event = VideoAnalysisCompletedEvent(
                    video_analysis_id=result["video_analysis_id"],
                    video_id=event.video_id,
                    report_id=event.report_id,
                    event_id=event.event_id,
                    success=result["success"],
                    fire_detected=result["fire_detected"],
                    error_message=None,
                )
                self.producer.publish(
                    TOPICS["video_analysis_completed"], completed_event
                )
            else:
                # Publish failed event
                failed_event = VideoAnalysisCompletedEvent(
                    video_id=event.video_id,
                    report_id=event.report_id,
                    event_id=event.event_id,
                    success=False,
                    error_message=result.get("error", "Unknown error"),
                )
                self.producer.publish(TOPICS["video_analysis_completed"], failed_event)

        except Exception as e:
            print(f"Error processing analysis requested event: {str(e)}")
            failed_event = VideoAnalysisCompletedEvent(
                video_id=event.video_id,
                report_id=event.report_id,
                event_id=event.event_id,
                success=False,
                error_message=str(e),
            )
            self.producer.publish(TOPICS["video_analysis_completed"], failed_event)

    async def _handle_analysis_completed(self, event: VideoAnalysisCompletedEvent):
        """Handle video analysis completed event."""
        try:
            print(f"Processing analysis completed event for video: {event.video_id}")
            # Import PolicyHandler here to avoid circular import
            from app.services.policy_handler import PolicyHandler

            policy_handler = PolicyHandler()

            # Process the event using policy handler
            result = await policy_handler.handle_event(
                "video_analysis_completed", event.model_dump()
            )
            print(f"Policy handler result: {result}")

            if result["success"]:
                print(f"Analysis completed successfully for video {result['video_id']}")
            else:
                print(
                    f"Analysis failed for video {result['video_id']}: {result.get('error')}"
                )

        except Exception as e:
            print(f"Error processing analysis completed event: {str(e)}")
