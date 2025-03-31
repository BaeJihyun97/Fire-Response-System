import ffmpeg
import os
import tempfile
from typing import Tuple
from pathlib import Path


class VideoTranscoder:

    async def transcode_video(
        self, file_content: bytes, video_id: str
    ) -> Tuple[bytes, str]:
        """
        Transcode video to 480p 30fps.
        Returns the transcoded video data and the output filename.
        """
        # Create temporary files for input and output
        with tempfile.NamedTemporaryFile(
            suffix=".mp4", delete=False
        ) as input_temp, tempfile.NamedTemporaryFile(
            suffix="_480p.mp4", delete=False
        ) as output_temp:

            try:
                # Write the input file content
                input_temp.write(file_content)
                input_temp.flush()

                # Transcode the video using ffmpeg
                stream = ffmpeg.input(input_temp.name)
                stream = ffmpeg.filter(stream, "scale", width=-1, height=480)
                stream = ffmpeg.output(
                    stream,
                    output_temp.name,
                    vcodec="mpeg4",  # Using mpeg4 codec which is widely supported
                    acodec="aac",
                    r=30,  # 30fps
                    strict="experimental",
                )

                # Run the ffmpeg command with error handling
                try:
                    ffmpeg.run(
                        stream,
                        overwrite_output=True,
                        capture_stdout=True,
                        capture_stderr=True,
                    )
                except ffmpeg.Error as e:
                    print(
                        f"FFmpeg stderr output: {e.stderr.decode() if e.stderr else 'No stderr output'}"
                    )
                    raise Exception(f"FFmpeg error: {str(e)}")

                # Verify the output file exists and has content
                if not os.path.exists(output_temp.name):
                    raise Exception("FFmpeg failed to create output file")

                if os.path.getsize(output_temp.name) == 0:
                    raise Exception("FFmpeg created an empty output file")

                # Read the transcoded video
                with open(output_temp.name, "rb") as f:
                    output_data = f.read()

                # Generate output filename using video_id
                output_filename = f"{video_id}_480p.mp4"

                return output_data, output_filename

            except Exception as e:
                print(f"Error in transcode_video: {str(e)}")
                raise
            finally:
                # Clean up temporary files
                try:
                    if os.path.exists(input_temp.name):
                        os.unlink(input_temp.name)
                    if os.path.exists(output_temp.name):
                        os.unlink(output_temp.name)
                except Exception as e:
                    print(f"Error cleaning up temporary files: {str(e)}")

    @staticmethod
    def get_video_info(video_path: str) -> dict:
        """Get video information using ffprobe."""
        try:
            probe = ffmpeg.probe(video_path)
            video_info = next(s for s in probe["streams"] if s["codec_type"] == "video")
            return {
                "width": int(video_info["width"]),
                "height": int(video_info["height"]),
                "duration": float(probe["format"]["duration"]),
                "bitrate": int(probe["format"]["bit_rate"]),
                "fps": eval(
                    video_info["r_frame_rate"]
                ),  # converts string like '30/1' to float
            }
        except Exception as e:
            print(f"Error getting video info: {str(e)}")
            return {}
