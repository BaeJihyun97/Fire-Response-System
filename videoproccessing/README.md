# Video Processing System

A video processing system that analyzes videos using OpenAI and Azure Video Indexer. The system uses event-driven architecture with Kafka for message passing and MongoDB for data storage.

## Features

- Video upload and storage in Azure Blob Storage
- Automatic video transcoding to 480p 30fps
- Video analysis using Azure Video Indexer
- Enhanced analysis using OpenAI
- Event-driven architecture with Kafka
- RESTful API with FastAPI
- MongoDB storage with Beanie ODM

## Prerequisites

- Python 3.9 or higher
- Poetry for dependency management
- MongoDB
- Kafka
- OpenAI API key
- Azure Video Indexer subscription
- Azure Blob Storage account
- FFmpeg installed on the system

## Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
# OpenAI Configuration
OPENAI_API_KEY=your_openai_api_key

# Azure Video Indexer Configuration
AZURE_VIDEO_INDEXER_ACCOUNT_ID=your_account_id
AZURE_VIDEO_INDEXER_SUBSCRIPTION_KEY=your_subscription_key
AZURE_VIDEO_INDEXER_LOCATION=your_location

# Azure Blob Storage Configuration
AZURE_STORAGE_CONNECTION_STRING=your_storage_connection_string
AZURE_STORAGE_CONTAINER_NAME=videos

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# MongoDB Configuration
MONGODB_URL=mongodb://localhost:27017
```

## Installation

1. Install FFmpeg:
```bash
# On macOS
brew install ffmpeg

# On Ubuntu
sudo apt-get update
sudo apt-get install ffmpeg
```

2. Clone the repository:
```bash
git clone <repository-url>
cd video-processing-system
```

3. Install dependencies using Poetry:
```bash
poetry install
```

4. Start the required services:
```bash
# Start MongoDB
docker run -d -p 27017:27017 mongo

# Start Kafka
docker-compose up -d
```

5. Start the application:
```bash
poetry run uvicorn app.main:app --reload
```

## API Endpoints

- `POST /videos`: Upload a new video
  - Request body:
    ```json
    {
        "video_url": "https://example.com/video.mp4",
        "system_id": "sys123"
    }
    ```
- `GET /videos/{video_id}`: Get video information
- `DELETE /videos/{video_id}`: Delete a video and its files
- `POST /videos/{video_id}/analyze`: Trigger video analysis
- `GET /videos/{video_id}/analysis`: Get video analysis reports

## Video Processing Flow

1. When a video is uploaded:
   - The original video is stored in Azure Blob Storage
   - A video document is created in MongoDB
   - The video is transcoded to 480p 30fps
   - The transcoded video is stored in Azure Blob Storage
   - The video document is updated with the transcoded video URL

2. When video analysis is triggered:
   - The video is analyzed using Azure Video Indexer
   - OpenAI enhances the analysis results
   - Results are stored in MongoDB
   - Events are published to Kafka for further processing

## Architecture

The system follows an event-driven architecture:

1. Videos are uploaded through the REST API
2. Video stored events are published to Kafka
3. Analysis is triggered asynchronously
4. Results are stored in MongoDB
5. Analysis completion events are published to Kafka

## Development

- Format code:
```bash
poetry run black .
poetry run isort .
```

- Run tests:
```bash
poetry run pytest
```

## License

MIT
