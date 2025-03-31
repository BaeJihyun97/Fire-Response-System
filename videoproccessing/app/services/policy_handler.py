from typing import Dict, Any
from app.models.video import Video
from app.services.video_analysis import VideoAnalysisService
from app.services.video_manager import VideoManager
import asyncio


class PolicyHandler:
    def __init__(self):
        self.video_analysis_service = VideoAnalysisService()
        self.video_manager = VideoManager()
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

    def handle_event(
        self, event_type: str, event_data: Dict[str, Any]
    ) -> Dict[str, Any]:
        """Handle different types of events based on event type."""
        try:
            if event_type == "video_stored":
                return self._handle_video_stored(event_data)
            elif event_type == "video_analysis_requested":
                return self._handle_analysis_requested(event_data)
            else:
                return {"success": False, "error": f"Unknown event type: {event_type}"}
        except Exception as e:
            return {"success": False, "error": str(e)}

    async def _handle_video_stored(self, event_data: Dict[str, Any]) -> Dict[str, Any]:
        """Handle video stored event."""
        pass

    async def _handle_analysis_requested(
        self, event_data: Dict[str, Any]
    ) -> Dict[str, Any]:
        """Handle video analysis requested event."""
        try:
            video_id = event_data.get("video_id")
            if not video_id:
                return {"success": False, "error": "Missing video_id in event data"}

            # Get video document
            video = await Video.find_one({"video_id": video_id})
            if not video:
                return {"success": False, "error": f"Video not found: {video_id}"}

            # Analyze video
            report = await self.video_analysis_service.analyze_video(video)
            if not report:
                return {
                    "success": False,
                    "error": "Failed to analyze video",
                    "video_id": video_id,
                }

            return {
                "success": True,
                "video_analysis_id": str(report.id),
                "fire_detected": report.openai_analysis["fire_detected"],
            }

        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "video_id": event_data.get("video_id"),
            }

    def _handle_analysis_completed(self, event_data: Dict[str, Any]) -> Dict[str, Any]:
        """Handle video analysis completed event."""
        try:
            video_id = event_data.get("video_id")
            success = event_data.get("success", False)
            error_message = event_data.get("error_message")

            if not video_id:
                return {"success": False, "error": "Missing video_id in event data"}

            if not success:
                print(f"Analysis failed for video {video_id}: {error_message}")
                return {"success": False, "error": error_message, "video_id": video_id}

            print(f"Analysis completed successfully for video {video_id}")
            return {"success": True, "video_id": video_id}

        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "video_id": event_data.get("video_id"),
            }

    async def _handle_video_deleted(self, event: any) -> Dict[str, Any]:
        """Handle video deleted event."""
        pass


# if __name__ == "__main__":
#     if kafka_consumer:
#         consumer_task = asyncio.create_task(kafka_consumer.start_consuming())
