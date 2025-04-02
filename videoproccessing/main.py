import os
import json
import time
import uuid
from datetime import datetime, timedelta
from kafka import KafkaProducer, KafkaConsumer
from dotenv import load_dotenv

# Kafka consumer setup
consumer = KafkaConsumer(
    "fire-detection-topic",
    bootstrap_servers=os.getenv("KAFKA_BROKER", "localhost:9092"),
    group_id=f"fire-detection-group-{uuid.uuid4()}",
    auto_offset_reset="earliest",
    enable_auto_commit=True,
    value_deserializer=lambda x: json.loads(x.decode("utf-8")),
)

# Set the time threshold (30 minutes ago)
time_threshold = datetime.now() - timedelta(minutes=30)

try:
    for message in consumer:
        # Get message timestamp
        message_timestamp = datetime.fromtimestamp(
            message.timestamp / 1000.0
        )  # Convert milliseconds to seconds

        # Skip messages older than 30 minutes
        if message_timestamp < time_threshold:
            print(f"Skipping old message from {message_timestamp}")
            continue

        data = message.value
        # ... existing code ...
except Exception as e:
    print(f"Error: {e}")
finally:
    consumer.close()
