from functools import lru_cache
from typing import Set
from pydantic_settings import BaseSettings
from pydantic import Field


class Settings(BaseSettings):
    # MongoDB
    mongodb_url: str = Field(default="mongodb://localhost:27017")
    mongodb_db_name: str = Field(default="video_processing")

    # Kafka
    kafka_bootstrap_servers: str = Field(default="localhost:9092")
    azure_event_hub_connection_string: str

    # Azure Blob Storage
    azure_storage_connection_string: str
    azure_storage_container_name: str = Field(default="videos")

    # Azure Video Indexer
    azure_video_indexer_account_id: str
    azure_video_indexer_account_name: str
    azure_video_indexer_subscription_id: str
    azure_video_indexer_subscription_key: str
    azure_video_indexer_location: str = Field(default="trial")
    azure_video_indexer_resource_group: str
    azure_tenant_id: str
    azure_client_id: str
    azure_client_secret: str

    # OpenAI
    openai_api_key: str

    # Application Settings
    max_file_size: int = Field(default=100 * 1024 * 1024)  # 100MB in bytes
    allowed_video_extensions: Set[str] = Field(default={".mp4", ".mov", ".avi", ".mkv"})

    # JWT Settings
    jwt_secret_key: str = Field(
        default="your-secret-key-here"
    )  # Change this in production!
    jwt_algorithm: str = Field(default="HS256")
    jwt_access_token_expire_minutes: int = Field(default=30)

    class Config:
        env_file = ".env"
        case_sensitive = False
        allow_extra = True


@lru_cache()
def get_settings() -> Settings:
    """Get cached settings instance."""
    return Settings()
