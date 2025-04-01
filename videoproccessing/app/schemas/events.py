from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_snake, to_camel


# Kafka topics
TOPICS = {
    "video_stored": "fireresponsesystem",
    "video_analysis_requested": "fireresponsesystem",
    "video_analysis_completed": "fireresponsesystem",
}


class VideoStoredEvent(BaseModel):
    video_id: int
    report_id: int
    original_video_uri: str
    encoded_video_uri: Optional[str]
    timestamp: datetime = datetime.utcnow()

    model_config = {
        "extra": "ignore",
        "alias_generator": to_camel,
        "populate_by_name": True,
    }


class VideoAnalysisRequestedEvent(BaseModel):
    video_id: int
    report_id: int
    event_id: int
    user_id: int

    model_config = ConfigDict(
        extra="ignore",
        alias_generator=to_camel,
        validate_by_name=True,
        validate_by_alias=True,
    )


class VideoAnalysisCompletedEvent(BaseModel):
    video_analysis_id: Optional[int]
    video_id: int
    report_id: int
    event_id: int
    success: bool
    fire_detected: bool
    tags: list[str] = []
    severity: Optional[str] = "N/A"
    timestamp: datetime = datetime.utcnow()

    model_config = {
        "extra": "ignore",
        "alias_generator": to_camel,
        "populate_by_name": True,
    }


class VideoBlurCompletedEvent(BaseModel):
    event_id: int
    blurred_video_uri: str
    timestamp: datetime = datetime.utcnow()

    model_config = {
        "extra": "ignore",
        "alias_generator": to_camel,
        "populate_by_name": True,
    }
