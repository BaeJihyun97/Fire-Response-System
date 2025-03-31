from datetime import datetime
from enum import Enum
from typing import Optional, List
from beanie import Document
from pydantic import BaseModel, Field
from pymongo import IndexModel, ASCENDING


class VideoStatus(str, Enum):
    PENDING = "pending"
    PROCESSING = "processing"
    COMPLETED = "completed"
    FAILED = "failed"


class VideoAnalysisTag(BaseModel):
    name: str
    confidence: float
    timestamp: float


class Counter(Document):
    """Model for managing auto-incrementing IDs"""

    collection_name: str
    seq: int = 0

    class Settings:
        name = "counters"


class Video(Document):
    video_id: int = Field(
        default_factory=lambda: None
    )  # Will be set by get_next_sequence
    report_id: Optional[int] = None
    original_video_uri: Optional[str] = None
    processed_video_uri: Optional[str] = None
    status: VideoStatus = VideoStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "videos"
        indexes = [IndexModel([("video_id", ASCENDING)], unique=True)]

    @classmethod
    async def get_next_sequence(cls) -> int:
        """Get the next sequence number for video_id"""
        counter = await Counter.find_one({"collection_name": "video_id"})
        if not counter:
            counter = Counter(collection_name="video_id", seq=1)
            await counter.save()
            return 1
        counter.seq += 1
        await counter.save()
        return counter.seq


class VideoAnalysisReport(Document):
    video_analysis_id: int = Field(
        default_factory=lambda: None
    )  # Will be set by get_next_sequence
    video_id: int
    report_id: int
    tags: List[VideoAnalysisTag] = []
    frame_urls: List[str] = []
    frame_count: int = 0
    created_at: datetime = Field(default_factory=datetime.utcnow)
    openai_analysis: Optional[dict] = None
    azure_analysis: Optional[dict] = None

    class Settings:
        name = "video_analysis_reports"
        indexes = [IndexModel([("video_analysis_id", ASCENDING)], unique=True)]

    @classmethod
    async def get_next_sequence(cls) -> int:
        """Get the next sequence number for video_analysis_id"""
        counter = await Counter.find_one({"collection_name": "video_analysis_id"})
        if not counter:
            counter = Counter(collection_name="video_analysis_id", seq=1)
            await counter.save()
            return 1
        counter.seq += 1
        await counter.save()
        return counter.seq
