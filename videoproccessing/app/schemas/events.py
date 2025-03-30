from datetime import datetime
from typing import Optional
from pydantic import BaseModel


# Kafka topics
TOPICS = {
    "video_stored": "video.stored",
    "video_analysis_requested": "video.analysis.requested",
    "video_analysis_completed": "video.analysis.completed"
}


class VideoStoredEvent(BaseModel):
    video_id: str
    system_id: str
    original_video_uri: str
    processed_video_uri: Optional[str]
    timestamp: datetime = datetime.utcnow()


class VideoAnalysisRequestedEvent(BaseModel):
    video_id: str
    report_id: str
    event_id: str


class VideoAnalysisCompletedEvent(BaseModel):
    video_analysis_id: Optional[str]
    video_id: str
    report_id: str
    event_id: str
    success: bool
    fire_detected: bool
    error_message: Optional[str]
    timestamp: datetime = datetime.utcnow()


class VideoBlurCompletedEvent(BaseModel):
    video_analysis_id: str
    report_id: str
    blurred_video_uri: str
    success: bool
    timestamp: datetime = datetime.utcnow()
