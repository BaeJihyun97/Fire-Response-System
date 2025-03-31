from app.models.video import Video, VideoStatus
from app.services.storage import AzureBlobStorage
from app.services.transcoder import VideoTranscoder
from app.core.config import get_settings


class VideoManager:
    def __init__(self):
        settings = get_settings()
        self.storage = AzureBlobStorage()
        self.transcoder = VideoTranscoder()

    async def delete_video(self, video: Video) -> bool:
        """Delete video files from storage and update document."""
        try:
            # Delete files from storage
            success = self.storage.delete_video(video.video_id)
            if not success:
                return False

            # Delete video document
            await video.delete()
            return True

        except Exception as e:
            print(f"Error deleting video: {str(e)}")
            return False

    async def process_video_file(self, report_id: int, file_content: bytes) -> Video:
        """
        Process a video file through the complete pipeline:
        1. Store original video in Azure Blob Storage
        2. Create video document in MongoDB
        3. Transcode video to 480p 30fps
        4. Update video document with transcoded video URL
        """
        video = None
        try:
            # Create video document with auto-incrementing ID
            video_id = await Video.get_next_sequence()
            video = Video(report_id=report_id, video_id=video_id)
            await video.save()

            # Process video
            processed_data, _ = await self.transcoder.transcode_video(
                file_content, video.video_id
            )

            # Upload videos to storage
            original_url = self.storage.upload_video(
                file_content, f"{video.video_id}/{video.video_id}.mp4"
            )
            processed_url = self.storage.upload_video(
                processed_data, f"{video.video_id}/{video.video_id}_480p.mp4"
            )

            # Update video document
            video.original_video_uri = original_url
            video.processed_video_uri = processed_url
            video.status = VideoStatus.COMPLETED
            await video.save()
            return video

        except Exception as e:
            print(f"Error processing video file: {str(e)}")
            if video:
                video.status = VideoStatus.FAILED
                await video.save()
            raise
