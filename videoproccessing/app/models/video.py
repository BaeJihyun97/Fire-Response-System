from datetime import datetime
from enum import Enum
from typing import Optional, List
from beanie import Document, Link
from pydantic import BaseModel, Field
from bson import ObjectId


class VideoStatus(str, Enum):
    PENDING = "pending"
    PROCESSING = "processing"
    COMPLETED = "completed"
    FAILED = "failed"


class VideoAnalysisTag(BaseModel):
    name: str
    confidence: float
    timestamp: float


class Video(Document):
    video_id: str = Field(default_factory=lambda: str(ObjectId()))
    report_id: Optional[str] = None
    original_video_uri: Optional[str] = None
    processed_video_uri: Optional[str] = None
    status: VideoStatus = VideoStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "videos"


class VideoAnalysisReport(Document):
    video_id: str
    report_id: str = Field(default_factory=lambda: str(ObjectId()))
    tags: List[VideoAnalysisTag] = []
    frame_urls: List[str] = []
    frame_count: int = 0
    duration: float = 0.0
    created_at: datetime = Field(default_factory=datetime.utcnow)
    openai_analysis: Optional[dict] = None
    azure_analysis: Optional[dict] = None

    class Settings:
        name = "video_analysis_reports"