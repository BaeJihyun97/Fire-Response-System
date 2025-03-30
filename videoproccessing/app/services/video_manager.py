from datetime import datetime
import aiohttp
from app.models.video import Video, VideoStatus
from app.services.storage import AzureBlobStorage
from app.services.transcoder import VideoTranscoder
from app.schemas.events import VideoStoredEvent
from app.core.config import get_settings


class VideoManager:
    def __init__(self):
        settings = get_settings()
        self.storage = AzureBlobStorage()
        self.transcoder = VideoTranscoder()

    async def process_video(self, video_url: str, system_id: str) -> Video:
        """
        Process a video through the complete pipeline:
        1. Store original video in Azure Blob Storage
        2. Create video document in MongoDB
        3. Transcode video to 480p 30fps
        4. Update video document with transcoded video URL
        """
        video = None
        try:
            # Create video document
            video = Video(system_id=system_id)
            await video.save()

            # Download and process video
            video_data = await self.transcoder.download_video(video_url)
            processed_data = await self.transcoder.transcode_video(video_data)

            # Upload videos to storage
            original_url = self.storage.upload_video(
                video_data,
                f"{video.video_id}/{video.video_id}.mp4"
            )
            processed_url = self.storage.upload_video(
                processed_data,
                f"{video.video_id}/{video.video_id}_480p.mp4"
            )

            # Update video document
            video.original_video_uri = original_url
            video.processed_video_uri = processed_url
            video.status = VideoStatus.COMPLETED
            await video.save()

            return video

        except Exception as e:
            print(f"Error processing video: {str(e)}")
            if video:
                video.status = VideoStatus.FAILED
                await video.save()
            raise

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

    async def process_video_file(self, report_id: str, file_content: bytes) -> Video:
        """
        Process a video file through the complete pipeline:
        1. Store original video in Azure Blob Storage
        2. Create video document in MongoDB
        3. Transcode video to 480p 30fps
        4. Update video document with transcoded video URL
        """
        video = None
        try:
            # Create video document
            video = Video(report_id=report_id)
            await video.save()

            # Process video
            processed_data = await self.transcoder.transcode_video(file_content, video.video_id)

            # Upload videos to storage
            original_url = self.storage.upload_video(
                file_content,
                f"{video.video_id}/{video.video_id}.mp4"
            )
            processed_url = self.storage.upload_video(
                processed_data,
                f"{video.video_id}/{video.video_id}_480p.mp4"
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