package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import fireresponsesystem.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class EventReportVideoViewHandler {

//<<< DDD / CQRS
    @Autowired
    private EventReportVideoRepository eventReportVideoRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReportReceived_then_CREATE_1 (@Payload ReportReceived reportReceived) {
        try {

            if (!reportReceived.validate()) return;

            // view 객체 생성
            EventReportVideo eventReportVideo = new EventReportVideo();
            // view 객체에 이벤트의 Value 를 set 함
            eventReportVideo.setReportId(Long.valueOf(reportReceived.getReportId()));
            eventReportVideo.setUserId(reportReceived.getUserId());
            eventReportVideo.setLongitude(reportReceived.getLongitude());
            eventReportVideo.setLatitude(reportReceived.getLatitude());
            eventReportVideo.setDescription(reportReceived.getDescription());
            // view 레파지 토리에 save
            eventReportVideoRepository.save(eventReportVideo);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenEventVideoIdUpdated_then_UPDATE_1(@Payload EventVideoIdUpdated eventVideoIdUpdated) {
        try {
            if (!eventVideoIdUpdated.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByReportId(Long.valueOf(eventVideoIdUpdated.getReportId()));
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setEventId(Long.valueOf(eventVideoIdUpdated.getEventId()));
                    eventReportVideo.setVideoId(eventVideoIdUpdated.getVideoId());
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReportUpdated_then_UPDATE_2(@Payload ReportUpdated reportUpdated) {
        try {
            if (!reportUpdated.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByReportId(Long.valueOf(reportUpdated.getReportId()));
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setStatus(reportUpdated.getStatus());
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // @StreamListener(KafkaProcessor.INPUT)
    // public void when_then_UPDATE_(@Payload  ) {
    //     try {
    //         if (!.validate()) return;
    //             // view 객체 조회

    //             List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByReportId(Long.valueOf(.getReportId()));
    //             for(EventReportVideo eventReportVideo : eventReportVideoList){
    //                 // view 객체에 이벤트의 eventDirectValue 를 set 함
    //                 eventReportVideo.setVideoId(.getVideoId());
    //                 eventReportVideo.setOriginalVideoUri(.getOriginalVideoUri());
    //                 eventReportVideo.setEncodedVideoUri(.getEncodedVideoUri());
    //                 eventReportVideo.set();
    //             // view 레파지 토리에 save
    //             eventReportVideoRepository.save(eventReportVideo);
    //             }

    //     }catch (Exception e){
    //         e.printStackTrace();
    //     }
    // }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenVideoAnalyzed_then_UPDATE_4(@Payload VideoAnalyzed videoAnalyzed) {
        try {
            if (!videoAnalyzed.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByVideoId(videoAnalyzed.getVideoId());
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setVideoAnalysisId(videoAnalyzed.getVideoAnalysisId());
                    eventReportVideo.setAnalyzeSuccess(videoAnalyzed.getSuccess());
                    eventReportVideo.setFireDetected(videoAnalyzed.getFireDetected());
                    eventReportVideo.setTags(videoAnalyzed.getTags());
                    eventReportVideo.setSeverity(videoAnalyzed.getSeverity());
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenEventCreated_then_UPDATE_5(@Payload EventCreated eventCreated) {
        try {
            if (!eventCreated.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByReportId(eventCreated.getReportId());
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setEventId(Long.valueOf(eventCreated.getEventId()));
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenAssignedToEvent_then_UPDATE_6(@Payload AssignedToEvent assignedToEvent) {
        try {
            if (!assignedToEvent.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByReportId(assignedToEvent.getReportId());
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setEventId(assignedToEvent.getEventId());
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenIdentifiedAsFireEvent_then_UPDATE_7(@Payload IdentifiedAsFireEvent identifiedAsFireEvent) {
        try {
            if (!identifiedAsFireEvent.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByEventId(Long.valueOf(identifiedAsFireEvent.getEventId()));
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setEventType("fire");
                    eventReportVideo.setVerified(identifiedAsFireEvent.getVerified());
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenIdentifiedAsNonFireEvent_then_UPDATE_8(@Payload IdentifiedAsNonFireEvent identifiedAsNonFireEvent) {
        try {
            if (!identifiedAsNonFireEvent.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByEventId(identifiedAsNonFireEvent.getEventId());
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setEventType("false");
                    eventReportVideo.setVerified(identifiedAsNonFireEvent.getVerified());
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPostPublished_then_UPDATE_9(@Payload PostPublished postPublished) {
        try {
            if (!postPublished.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByEventId(Long.valueOf(postPublished.getEventId()));
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setPostId(Long.valueOf(postPublished.getPostId()));
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReacted_then_UPDATE_10(@Payload Reacted reacted) {
        try {
            if (!reacted.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByPostId(Long.valueOf(reacted.getPostId()));
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setReatCount(eventReportVideo.getReatCount() + 1);
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCommentAdded_then_UPDATE_11(@Payload CommentAdded commentAdded) {
        try {
            if (!commentAdded.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByPostId(Long.valueOf(commentAdded.getPostId()));
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setCommentCount(eventReportVideo.getCommentCount() + 1);
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenVideoAnalysisFailed_then_UPDATE_12(@Payload VideoAnalysisFailed videoAnalysisFailed) {
        try {
            if (!videoAnalysisFailed.validate()) return;
                // view 객체 조회

                List<EventReportVideo> eventReportVideoList = eventReportVideoRepository.findByVideoId(videoAnalysisFailed.getVideoId());
                for(EventReportVideo eventReportVideo : eventReportVideoList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    eventReportVideo.setAnalyzeSuccess(false);
                // view 레파지 토리에 save
                eventReportVideoRepository.save(eventReportVideo);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


//>>> DDD / CQRS
}

