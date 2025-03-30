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
