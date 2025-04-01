from azure.storage.blob import BlobServiceClient, generate_blob_sas, BlobSasPermissions
from urllib.parse import urlparse
from datetime import datetime, timedelta
from app.core.config import get_settings


class AzureBlobStorage:
    def __init__(self):
        settings = get_settings()
        self.connection_string = settings.azure_storage_connection_string
        self.container_name = settings.azure_storage_container_name
        self.blob_service_client = BlobServiceClient.from_connection_string(
            self.connection_string
        )
        self.container_client = self.blob_service_client.get_container_client(
            self.container_name
        )

    def upload_video(self, file_content: bytes, blob_path: str) -> str:
        """Upload a video file to Azure Blob Storage."""
        try:
            # Get blob client
            blob_client = self.container_client.get_blob_client(blob_path)

            # Upload the video file
            blob_client.upload_blob(file_content, overwrite=True)

            # Generate SAS URL for the uploaded video
            sas_token = generate_blob_sas(
                account_name=self.blob_service_client.account_name,
                container_name=self.container_name,
                blob_name=blob_path,
                account_key=self.blob_service_client.credential.account_key,
                permission=BlobSasPermissions(read=True),
                expiry=datetime.utcnow() + timedelta(hours=24),
            )

            # Construct the full URL
            blob_url = f"https://{self.blob_service_client.account_name}.blob.core.windows.net/{self.container_name}/{blob_path}"  # ?{sas_token}"
            return blob_url

        except Exception as e:
            print(f"Error uploading video: {str(e)}")
            raise

    def download_video(self, processed_video_uri: str, local_path: str):
        """Download a video file from Azure Blob Storage."""
        try:
            print(f"Downloading video from URI: {processed_video_uri}")

            # Extract blob name from the URI
            parsed_url = urlparse(processed_video_uri)
            # Remove the container name from the path
            path_parts = parsed_url.path.split("/")
            if len(path_parts) > 2:
                blob_name = "/".join(
                    path_parts[2:]
                )  # Skip container name and leading slash
            else:
                blob_name = path_parts[-1]

            print(f"Extracted blob name: {blob_name}")
            blob_client = self.container_client.get_blob_client(blob_name)

            # Check if blob exists
            if not blob_client.exists():
                print(f"Blob does not exist: {blob_name}")
                raise Exception(f"Blob not found: {blob_name}")

            # Download the video file
            with open(local_path, "wb") as data:
                data.write(blob_client.download_blob().readall())
            print(f"Successfully downloaded blob to: {local_path}")

        except Exception as e:
            print(f"Error downloading video: {str(e)}")
            raise

    def upload_frame(self, video_id: str, frame_number: int, frame_path: str) -> str:
        """Upload a frame to Azure Blob Storage."""
        try:
            # Create a folder structure using video_id
            blob_name = f"{video_id}/frames/frame_{frame_number}.jpg"
            blob_client = self.container_client.get_blob_client(blob_name)

            # Upload the frame
            with open(frame_path, "rb") as data:
                blob_client.upload_blob(data, overwrite=True)

            # Generate SAS URL for the uploaded frame
            sas_token = generate_blob_sas(
                account_name=self.blob_service_client.account_name,
                container_name=self.container_name,
                blob_name=blob_name,
                account_key=self.blob_service_client.credential.account_key,
                permission=BlobSasPermissions(read=True),
                expiry=datetime.utcnow() + timedelta(hours=24),
            )

            # Construct the full URL
            blob_url = f"https://{self.blob_service_client.account_name}.blob.core.windows.net/{self.container_name}/{blob_name}?{sas_token}"
            return blob_url

        except Exception as e:
            print(f"Error uploading frame: {str(e)}")
            raise

    def delete_video(self, video_id: str):
        """Delete a video and its associated frames from Azure Blob Storage."""
        try:
            # Delete the original video
            video_blob_name = f"{video_id}/original.mp4"
            video_blob_client = self.container_client.get_blob_client(video_blob_name)
            video_blob_client.delete_blob()

            # Delete all frames
            frames_prefix = f"{video_id}/frames/"
            for blob in self.container_client.list_blobs(
                name_starts_with=frames_prefix
            ):
                blob_client = self.container_client.get_blob_client(blob.name)
                blob_client.delete_blob()

        except Exception as e:
            print(f"Error deleting video and frames: {str(e)}")
            raise

    def get_blob_name(self, video_url: str) -> str:
        """Extract blob name from video URL."""
        parsed_url = urlparse(video_url)
        return parsed_url.path.split("/")[-1]
