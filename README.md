# 

## Model
www.msaez.io/#/70127344/storming/fire-response-system2

## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd infra
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- reportmanagement
- videoproccessing
- eventmanagement
- aianalysis
- feedservice
- notification


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- reportmanagement
```
 http :8088/reports reportId="reportId" eventId="eventId" userId="userId" longitude="longitude" latitude="latitude" videoUri="videoUri" description="description" uploadedAt="uploadedAt" status="status" 
```
- videoproccessing
```
 http :8088/videoAnalyses videoAnalysisId="videoAnalysisId" reportId="reportId" blurredVideoUri="blurredVideoUri" fireDetected="fireDetected" tags="tags" summary="summary" frames="frames" 
 http :8088/videos videoId="videoId" reportId="reportId" originalVideoUri="originalVideoUri" encodedVideoUri="encodedVideoUri" 
```
- eventmanagement
```
 http :8088/events eventId="eventId" longitude="longitude" latitude="latitude" status="status" eventType="eventType" createdAt="createdAt" resolvedAt="resolvedAt" 
```
- aianalysis
```
 http :8088/aiAnalyses analysisId="analysisId" riskLevel="riskLevel" resources="resources" response="response" eventId="eventId" status="status" 
```
- feedservice
```
 http :8088/posts postId="postId" blurredVideoUri="blurredVideoUri" userId="userId" eventId="eventId" comments="comments" reactionCount="reactionCount" createdAt="createdAt" updatedAt="updatedAt" 
```
- notification
```
 http :8088/notifications id="id" 
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```

