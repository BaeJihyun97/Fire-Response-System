import asyncio
import logging
from typing import Optional
import aiohttp
from azure.identity import ClientSecretCredential


class VideoIndexerClient:
    """Client for interacting with Azure Video Indexer API."""

    def __init__(
        self,
        account_id: str,
        account_name: str,
        resource_group: str,
        subscription_id: str,
        location: str,
        tenant_id: str,
        client_id: str,
        client_secret: str,
        api_version: str = "2025-01-01",
    ):
        self.account_id = account_id
        self.account_name = account_name
        self.resource_group = resource_group
        self.subscription_id = subscription_id
        self.location = location
        self.api_version = api_version
        self._credential = ClientSecretCredential(
            tenant_id=tenant_id, client_id=client_id, client_secret=client_secret
        )
        self.tenant_id = tenant_id
        self.client_id = client_id
        self.client_secret = client_secret
        self._base_url = (
            f"https://api.videoindexer.ai/{self.location}/Accounts/{self.account_id}"
        )

    async def _get_access_token(self) -> str:
        """Get access token for Video Indexer API."""
        scope = "https://management.azure.com/.default"
        grant_type = "client_credentials"
        async with aiohttp.ClientSession() as session:
            token_url = (
                f"https://login.microsoftonline.com/{self.tenant_id}/oauth2/v2.0/token"
            )
            data = {
                "grant_type": grant_type,
                "client_id": self.client_id,
                "client_secret": self.client_secret,
                "scope": scope,
            }
            headers = {"Content-Type": "application/x-www-form-urlencoded"}
            async with session.post(token_url, headers=headers, data=data) as response:
                response.raise_for_status()
                result = await response.json()
                return result["access_token"]

    async def _get_access_token_for_video_api(self) -> str:
        """Get access token specifically for Video Indexer operations."""
        access_token = await self._get_access_token()
        async with aiohttp.ClientSession() as session:
            token_url = f"https://management.azure.com/subscriptions/{self.subscription_id}/resourceGroups/{self.resource_group}/providers/Microsoft.VideoIndexer/accounts/{self.account_name}/generateAccessToken?api-version={self.api_version}"
            headers = {
                "Authorization": f"Bearer {access_token}",
                "Content-Type": "application/json",
            }
            body = {
                "permissionType": "Contributor",
                "scope": "Account",
            }
            async with session.post(token_url, headers=headers, json=body) as response:
                response.raise_for_status()
                result = await response.json()
                return result["accessToken"]

    async def upload_video(
        self,
        video_url: str,
        privacy_mode: str = "Private",
        video_name: Optional[str] = None,
    ) -> str:
        """Upload a video to Video Indexer using a URL.

        Args:
            video_url: Azure Storage URL of the video
            privacy_mode: Privacy setting for the video ("Private" or "Public")
            video_name: Optional name for the video (defaults to URL filename)

        Returns:
            str: Video ID of the uploaded video
        """
        access_token = await self._get_access_token_for_video_api()
        excluded_ai = [
            "Emotions",
            "RollingCredits",
            "DetectedObjects",
            "Celebrities",
            "KnownPeople",
            "OCR",
            "Clapperboard",
            "Logos",
            "Speakers",
            "Entities",
            "FeaturedClothing",
            "ShotType",
            "PeopleDetectedClothing",
            "Labels",
            "ObservedPeople",
            "Topics",
            "Keywords",
            "MatchedPerson",
        ]
        async with aiohttp.ClientSession() as session:
            url = f"{self._base_url}/Videos"
            params = {
                "accessToken": access_token,
                "name": video_name,
                "privacy": privacy_mode,
                "videoUrl": video_url,
                "excludedAI": excluded_ai,
            }

            async with session.post(url, params=params) as response:
                response.raise_for_status()
                result = await response.json()
                return result["id"]

    async def blur_faces(self, video_id: str, video_name: str) -> str:
        """Blur faces in a video.

        Args:
            video_id: ID of the video to process
        """
        access_token = await self._get_access_token_for_video_api()

        async with aiohttp.ClientSession() as session:
            url = f"{self._base_url}/Videos/{video_id}/redact"
            params = {
                "accessToken": access_token,
                "name": video_name,
                "externalId": video_id,
            }
            json = {"faces": {"blurringKind": "BoundingBox"}}

            async with session.post(url, params=params, json=json) as response:
                response.raise_for_status()
                location = response.headers["Location"]
                return location.split("/")[-1]

    async def wait_for_index_processing(
        self, video_id: str, polling_interval: int = 10
    ) -> None:
        """Wait for video processing to complete.

        Args:
            video_id: ID of the video to check
            polling_interval: Time in seconds between status checks
        """
        while True:
            status = await self.get_index_status(video_id)
            if status == "Processed":
                break
            elif status in ["Failed", "Error"]:
                raise Exception(f"Video processing failed with status: {status}")
            await asyncio.sleep(polling_interval)

    async def wait_for_job(self, job_id: str, polling_interval: int = 10) -> None:
        """Wait for video processing to complete.

        Args:
            video_id: ID of the video to check
            polling_interval: Time in seconds between status checks
        """
        while True:
            try:
                status = await self.get_job_status(job_id)
            except:
                print("Job completed.")
                break
            if status == 1:
                print("Job completed.")
                break
            elif status in [2, 3, 4, 5]:
                raise Exception(f"Video processing failed with status: {status}")
            await asyncio.sleep(polling_interval)

    async def get_index_status(self, video_id: str) -> str:
        """Get the processing status of a video.

        Args:
            video_id: ID of the video to check

        Returns:
            str: Processing status
        """
        access_token = await self._get_access_token_for_video_api()

        async with aiohttp.ClientSession() as session:
            url = f"{self._base_url}/Videos/{video_id}/Index"
            params = {"accessToken": access_token}

            async with session.get(url, params=params) as response:
                response.raise_for_status()
                result = await response.json()
                return result["state"]

    async def get_job_status(self, job_id: str) -> str:
        access_token = await self._get_access_token_for_video_api()

        async with aiohttp.ClientSession() as session:
            url = f"{self._base_url}/Jobs/{job_id}"
            params = {"accessToken": access_token}

            async with session.get(url, params=params) as response:
                response.raise_for_status()
                result = await response.json()
                return result["state"]

    async def delete_video(self, video_id: str) -> None:
        """Delete a video from Video Indexer.

        Args:
            video_id: ID of the video to delete
        """
        access_token = await self._get_access_token_for_video_api()

        async with aiohttp.ClientSession() as session:
            url = f"{self._base_url}/Videos/{video_id}"
            params = {"accessToken": access_token}

            async with session.delete(url, params=params) as response:
                response.raise_for_status()

    async def get_video_id_by_external_id(self, external_id: str) -> str:
        """Get the Video Indexer ID for a video by external ID.

        Args:
            external_id: External ID of the video

        Returns:
            str: Video ID
        """
        access_token = await self._get_access_token_for_video_api()

        async with aiohttp.ClientSession() as session:
            url = f"{self._base_url}/Videos/GetIdByExternalId"
            params = {"accessToken": access_token, "externalId": external_id}

            async with session.get(url, params=params) as response:
                response.raise_for_status()
                result = await response.json()
                return result
