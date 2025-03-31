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
        self.max_frames = 2  # Maximum number of frames to extract
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

    async def analyze_video(self, video: Video) -> Optional[VideoAnalysisReport]:
        """Analyze a video and extract frames."""
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

                # Create analysis report
                report = VideoAnalysisReport(
                    video_id=video.video_id,
                    report_id=video.report_id,
                    frame_count=len(frames),
                )
                report.video_analysis_id = await VideoAnalysisReport.get_next_sequence()
                await report.save()

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
            fps = int(cap.get(cv2.CAP_PROP_FPS))
            width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
            height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
            duration = total_frames / fps

            print(
                f"Video properties: {total_frames} frames, {fps} fps, {width}x{height}, duration: {duration:.2f}s"
            )

            # Calculate frame indices for equal spacing
            frame_indices = np.linspace(0, total_frames - 1, self.max_frames, dtype=int)
            print(f"Extracting frames at indices: {frame_indices}")

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
            "location": {
                "indoor": any(r["location"]["indoor"] for r in results),
                "environment_description": majority_vote(
                    [r["location"]["environment_description"] for r in results]
                ),
                "nearby_objects": sorted(
                    set(obj for r in results for obj in r["location"]["nearby_objects"])
                ),
            },
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
                "tools_visible": sorted(
                    set(
                        tool
                        for r in results
                        for tool in r["firefighting_response"]["tools_visible"]
                    )
                ),
            },
            "lighting_conditions": majority_vote(
                [r["lighting_conditions"] for r in results]
            ),
        }

        return merged

    def _analyze_frames_with_llm(self, frames: List[str]) -> dict:
        """Analyze frames using GPT-4V model."""
        gpt4o_model = ChatOpenAI(
            model="gpt-4o", temperature=0.1, openai_api_key=self.settings.openai_api_key
        )

        system_prompt = """
        You are an expert fire analysis assistant. Analyze the provided image frame carefully and respond strictly with the following JSON schema. But MODIFY the values according to what you observe in the image. Do NOT include any additional text or explanation.

        {{
        "fire_detected": true,
        "fire_size": "moderate",
        "flame_color": ["orange", "yellow"],
        "smoke": {{
            "present": true,
            "color": "black",
            "density": "thick"
        }},
        "location": {{
            "indoor": false,
            "environment_description": "Urban street at night",
            "nearby_objects": ["car", "streetlight", "sidewalk"]
        }},
        "people_or_animals": {{
            "present": true,
            "details": "Two people running from the fire, no visible injuries"
        }},
        "fire_spread": {{
            "spreading": true,
            "indicators": ["thick smoke rising", "flames engulfing car"]
        }},
        "firefighting_response": {{
            "responders_present": false,
            "tools_visible": []
        }},
        "lighting_conditions": "Nighttime with fire providing illumination"
        }}
        """

        # Setup prompt template
        # prompt = ChatPromptTemplate.from_messages([
        #     ("system", system_prompt),
        #     ("human", "Analyze these image frames: {frames}")
        # ])

        system_message = SystemMessage(content=system_prompt)

        # Use JSON parser to parse structured output
        parser = SimpleJsonOutputParser()

        # Build LangChain pipeline
        # analyze_chain = prompt | gpt4o_model | parser

        results = []

        batches = self._batch_frames_by_token_limit(frames)

        for i, batch in enumerate(batches):
            print(f"Analyzing batch {i+1}/{len(batches)}...")
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
                print(f"Error analyzing frame {i+1}: {e}")

        # Print the structured result as Python dictionary
        merged_result = self._merge_fire_frame_analyses(results)
        return merged_result
