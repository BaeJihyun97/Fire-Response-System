import os
import base64
import tempfile
import tiktoken
from collections import Counter
from typing import Optional, List
from app.models.video import Video, VideoAnalysisReport, VideoStatus
from app.core.config import get_settings
from app.services.storage import AzureBlobStorage
import cv2
import numpy as np
import asyncio
from langchain_openai import ChatOpenAI
from langchain.output_parsers.json import SimpleJsonOutputParser
from langchain_core.messages import SystemMessage, HumanMessage


class VideoAnalysisService:
    def __init__(self):
        self.settings = get_settings()
        self.storage = AzureBlobStorage()
        self.frame_size = (640, 480)  # Target frame size
        self.max_frames = 30  # Maximum number of frames to extract
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

    async def analyze_video(self, video: Video) -> Optional[VideoAnalysisReport]:
        """Analyze a video and extract frames."""
        # Create analysis report
        report = VideoAnalysisReport(
            video_id=video.video_id,
            report_id=video.report_id,
        )
        report.video_analysis_id = await VideoAnalysisReport.get_next_sequence()
        await report.save()
        try:
            # Create a temporary directory for frame storage
            with tempfile.TemporaryDirectory() as temp_dir:
                # Download video to temporary file
                video_path = os.path.join(temp_dir, "video.mp4")
                self.storage.download_video(video.processed_video_uri, video_path)

                # Extract frames using OpenCV
                frames = self._extract_frames(video_path)

                if not frames:
                    print("No frames extracted from video")
                    return None

                # Upload frames to storage
                encoded_frames = []
                for i, frame in enumerate(frames):
                    frame_path = os.path.join(temp_dir, f"frame_{i}.jpg")
                    cv2.imwrite(frame_path, frame)
                    base64_string = self._encode_image(frame_path)
                    encoded_frames.append(base64_string)
                    frame_url = self.storage.upload_frame(
                        video_id=video.video_id, frame_number=i, frame_path=frame_path
                    )
                    report.frame_urls.append(frame_url)

                # Analyze frames using GPT-4o model
                analysis = self._analyze_frames_with_llm(encoded_frames)
                report.openai_analysis = analysis
                report.status = VideoStatus.COMPLETED

                # Save the report
                await report.save()
                return report

        except Exception as e:
            print(f"Error analyzing video: {str(e)}")
            report.status = VideoStatus.FAILED
            await report.save()
            return None

    def _extract_frames(self, video_path: str) -> List[np.ndarray]:
        """Extract frames from video using OpenCV."""
        frames = []

        try:
            # Open video file
            cap = cv2.VideoCapture(video_path)

            if not cap.isOpened():
                print(f"Error opening video file: {video_path}")
                return frames

            # Get video properties
            total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))

            # Calculate frame indices for equal spacing
            frame_indices = np.linspace(0, total_frames - 1, self.max_frames, dtype=int)

            # Extract frames at calculated indices
            for frame_idx in frame_indices:
                # Set frame position
                cap.set(cv2.CAP_PROP_POS_FRAMES, frame_idx)
                ret, frame = cap.read()

                if not ret:
                    print(f"Failed to read frame at index {frame_idx}")
                    continue

                # Resize frame if needed
                if frame.shape[:2] != self.frame_size[::-1]:
                    # Calculate aspect ratio
                    h, w = frame.shape[:2]
                    target_w, target_h = self.frame_size
                    aspect_ratio = w / h

                    # Calculate new dimensions maintaining aspect ratio
                    if aspect_ratio > 1:  # wider than tall
                        new_w = target_w
                        new_h = int(target_w / aspect_ratio)
                    else:  # taller than wide
                        new_h = target_h
                        new_w = int(target_h * aspect_ratio)

                    # Resize maintaining aspect ratio
                    frame = cv2.resize(frame, (new_w, new_h))

                    # Create a black background of target size
                    background = np.zeros((target_h, target_w, 3), dtype=np.uint8)

                    # Calculate position to center the resized frame
                    y_offset = (target_h - new_h) // 2
                    x_offset = (target_w - new_w) // 2

                    # Place the resized frame in the center of the background
                    background[
                        y_offset : y_offset + new_h, x_offset : x_offset + new_w
                    ] = frame
                    frame = background

                frames.append(frame)

            cap.release()
            print(f"Extracted {len(frames)} frames from video")

        except Exception as e:
            print(f"Error extracting frames: {str(e)}")

        return frames

    def _encode_image(self, image_path: str):
        with open(image_path, "rb") as image_file:
            return base64.b64encode(image_file.read()).decode("utf-8")

    def _estimate_token_count(self, text: str, model: str = "gpt-4o") -> int:
        enc = tiktoken.encoding_for_model(model)
        return len(enc.encode(text))

    def _batch_frames_by_token_limit(
        self, base64_frames: list, max_tokens: int = 100000
    ) -> list[list[str]]:
        batches = []
        current_batch = []
        current_token_total = 0

        for b64_img in base64_frames:
            est_tokens = self._estimate_token_count(b64_img)
            if current_token_total + est_tokens > max_tokens:
                batches.append(current_batch)
                current_batch = [b64_img]
                current_token_total = est_tokens
            else:
                current_batch.append(b64_img)
                current_token_total += est_tokens

        if current_batch:
            batches.append(current_batch)

        return batches

    def _merge_fire_frame_analyses(self, results):
        def majority_vote(values):
            return Counter(values).most_common(1)[0][0]

        merged = {
            "fire_detected": any(r["fire_detected"] for r in results),
            "fire_size": majority_vote([r["fire_size"] for r in results]),
            "flame_color": sorted(
                set(color for r in results for color in r["flame_color"])
            ),
            "smoke": {
                "present": any(r["smoke"]["present"] for r in results),
                "color": majority_vote([r["smoke"]["color"] for r in results]),
                "density": majority_vote([r["smoke"]["density"] for r in results]),
            },
            "objects": sorted(set(obj for r in results for obj in r["objects"])),
            "people_or_animals": {
                "present": any(r["people_or_animals"]["present"] for r in results),
                "details": majority_vote(
                    [r["people_or_animals"]["details"] for r in results]
                ),
            },
            "fire_spread": {
                "spreading": any(r["fire_spread"]["spreading"] for r in results),
                "indicators": sorted(
                    set(i for r in results for i in r["fire_spread"]["indicators"])
                ),
            },
            "firefighting_response": {
                "responders_present": any(
                    r["firefighting_response"]["responders_present"] for r in results
                ),
            },
            "severity": majority_vote([r["severity"] for r in results]),
        }

        return merged

    def _analyze_frames_with_llm(self, frames: List[str]) -> dict:
        """Analyze frames using GPT-4V model."""
        gpt4o_model = ChatOpenAI(
            model="gpt-4o", temperature=0.1, openai_api_key=self.settings.openai_api_key
        )

        system_prompt = """
        You are an expert fire analysis assistant. Analyze the provided image frame carefully and respond strictly with the following JSON schema. But MODIFY the values according to what you observe in the image. Do NOT include any additional text or explanation.

        Important rules:
        - Only set "fire_detected": true **if there is visible fire and/or smoke that is clearly from a fire** (e.g. flames, black smoke from burning material).
        - Do **not** set "fire_detected": true for ordinary smoke such as from factory exhaust, chimneys, vehicles, or other non-fire sources.
        - Use only the following options for certain fields:
            - "fire_detected": [true, false]
            - "severity": ["N/A", "낮음", "중간", "높음"]
            - "fire_size": ["N/A", "소형", "중형", "대형", "초대형"]
        - Return with the following JSON schema EVEN IF there is no fire.

        {{
        "fire_detected": true,
        "fire_size": "중형",
        "flame_color": ["주황", "노랑"],
        "smoke": {{
            "present": true,
            "color": "검은색",
            "density": "높음"
        }},
        "objects": ["차", "가로등", "인도", "나무"],
        "people_or_animals": {{
            "present": true,
            "details": "3명 이하의 사람이 화재로부터 달아나고 있음. 겉으로 드러나는 부상은 없어 보임."
        }},
        "fire_spread": {{
            "spreading": true,
            "indicators": ["짙은 연기가 피어오름", "차량 전소"]
        }},
        "firefighting_response": {{
            "responders_present": false,
        }},
        "severity": "중간"
        }}
        """

        system_message = SystemMessage(content=system_prompt)

        # Use JSON parser to parse structured output
        parser = SimpleJsonOutputParser()

        results = []

        batches = self._batch_frames_by_token_limit(frames)

        for i, batch in enumerate(batches):
            content = [{"type": "text", "text": "Analyze the following image frames:"}]
            for frame in batch:
                content.append(
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{frame}",
                            "detail": "high",
                        },
                    }
                )
            human_message = HumanMessage(content=content)

            try:
                response = gpt4o_model.invoke([system_message, human_message])
                parsed = parser.invoke(response.content)
                results.append(parsed)

            except Exception as e:
                print(f"Error analyzing frame {i+1}/{len(batches)}: {e}")

        # Print the structured result as Python dictionary
        merged_result = self._merge_fire_frame_analyses(results)
        return merged_result
