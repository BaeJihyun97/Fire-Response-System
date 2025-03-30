import os
from typing import List
from fastapi import FastAPI, HTTPException, BackgroundTasks, File, UploadFile, Form, Depends
from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient
from pydantic import BaseModel
import asyncio
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from app.core.config import Settings, get_settings
from app.models.video import Video, VideoAnalysisReport
from app.schemas.events import (
    VideoAnalysisCompletedEvent,
    TOPICS
)
from app.messaging.kafka import KafkaProducer, KafkaConsumer
from app.services.video_analysis import VideoAnalysisService
from app.services.video_manager import VideoManager


class VideoUploadRequest(BaseModel):
    video_url: str
    system_id: str


class VideoUploadResponse(BaseModel):
    video_id: str
    original_video_uri: str
    status: str


# Initialize services
kafka_producer = None
video_analysis_service = None
video_manager = None
kafka_consumer = None
consumer_task = None
shutdown_event = asyncio.Event()


async def startup():
    """Initialize services and database connection."""
    settings = get_settings()

    # Initialize global services
    global kafka_producer, video_analysis_service, video_manager, kafka_consumer
    kafka_producer = KafkaProducer(
        bootstrap_servers=settings.kafka_bootstrap_servers
    )
    video_analysis_service = VideoAnalysisService()
    video_manager = VideoManager()
    kafka_consumer = KafkaConsumer(
        bootstrap_servers=settings.kafka_bootstrap_servers
    )

    # Initialize MongoDB connection
    client = AsyncIOMotorClient(settings.mongodb_url)
    await init_beanie(
        database=client[settings.mongodb_db_name],
        document_models=[Video, VideoAnalysisReport]
    )


async def shutdown():
    """Cleanup services and close connections."""
    global kafka_consumer, consumer_task
    print("Starting shutdown process...")
    if kafka_consumer:
        print("Stopping Kafka consumer...")
        kafka_consumer.stop()
    if consumer_task:
        print("Cancelling consumer task...")
        consumer_task.cancel()
        try:
            await consumer_task
        except asyncio.CancelledError:
            pass
    print("Shutdown complete.")


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Initialize services first
    await startup()

    # Start the Kafka consumer in the background
    global consumer_task
    if kafka_consumer:
        print("Starting Kafka consumer in lifespan...")
        consumer_task = asyncio.create_task(kafka_consumer.start_consuming())
    yield

    # Shutdown
    print("Initiating shutdown...")
    kafka_consumer.stop()
    shutdown_event.set()  # Notify consumer to stop
    consumer_task.cancel()
    try:
        await consumer_task
    except asyncio.CancelledError:
        print("Consumer task cancelled successfully.")
    await shutdown()


app = FastAPI(title="Video Processing System", lifespan=lifespan)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, replace with specific origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.post("/videos/upload", response_model=VideoUploadResponse)
async def upload_video(
    file: UploadFile = File(...),
    report_id: str = Form(...),
    settings: Settings = Depends(get_settings)
):
    """Upload a video file directly."""
    # Validate file extension
    file_ext = os.path.splitext(file.filename)[1].lower()
    if file_ext not in settings.allowed_video_extensions:
        raise HTTPException(
            status_code=400,
            detail=f"File type not allowed. Allowed types: {', '.join(settings.allowed_video_extensions)}"
        )

    try:
        # Read file content
        content = await file.read()

        # Check file size
        if len(content) > settings.max_file_size:
            raise HTTPException(
                status_code=400,
                detail=f"File too large. Maximum size allowed: {settings.max_file_size/1024/1024}MB"
            )

        # Process video using video manager
        video = await video_manager.process_video_file(report_id=report_id, file_content=content)

        return VideoUploadResponse(
            video_id=video.video_id,
            original_video_uri=video.original_video_uri,
            status=video.status
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        await file.close()


@app.get("/videos/{video_id}", response_model=Video)
async def get_video(video_id: str):
    """Get video information."""
    video = await Video.find_one({"video_id": video_id})
    if not video:
        raise HTTPException(status_code=404, detail="Video not found")
    return video


@app.delete("/videos/{video_id}")
async def delete_video(video_id: str):
    """Delete a video and its associated files."""
    video = await Video.find_one({"video_id": video_id})
    if not video:
        raise HTTPException(status_code=404, detail="Video not found")

    success = await video_manager.delete_video(video)
    if not success:
        raise HTTPException(status_code=500, detail="Failed to delete video")

    return {"message": "Video deleted successfully"}


@app.post("/videos/{video_id}/analyze")
async def analyze_video(video_id: str, background_tasks: BackgroundTasks):
    """Trigger video analysis for a specific video."""
    video = await Video.find_one({"video_id": video_id})
    if not video:
        raise HTTPException(status_code=404, detail="Video not found")

    # Create analysis report
    report = VideoAnalysisReport(video_id=video_id)
    await report.save()

    # Start analysis in background
    background_tasks.add_task(process_video_analysis, video, report)

    return {"message": "Video analysis started", "report_id": report.report_id}


@app.get("/videos/{video_id}/analysis", response_model=List[VideoAnalysisReport])
async def get_video_analysis(video_id: str):
    """Get all analysis reports for a specific video."""
    reports = await VideoAnalysisReport.find({"video_id": video_id}).to_list()
    if not reports:
        raise HTTPException(status_code=404, detail="No analysis reports found")
    return reports


async def process_video_analysis(video: Video, report: VideoAnalysisReport):
    """Background task to process video analysis."""
    try:
        updated_report = await video_analysis_service.analyze_video(video)

        if updated_report:
            event = VideoAnalysisCompletedEvent(
                video_id=video.video_id,
                report_id=report.report_id,
                success=True
            )
        else:
            event = VideoAnalysisCompletedEvent(
                video_id=video.video_id,
                report_id=report.report_id,
                success=False,
                error_message="Analysis failed"
            )

        kafka_producer.publish(TOPICS["video_analysis_completed"], event)

    except Exception as e:
        print(f"Error processing video analysis: {str(e)}")
        event = VideoAnalysisCompletedEvent(
            video_id=video.video_id,
            report_id=report.report_id,
            success=False,
            error_message=str(e)
        )
        kafka_producer.publish(TOPICS["video_analysis_completed"], event)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8001,
        reload=True,
        log_level="info"
    )