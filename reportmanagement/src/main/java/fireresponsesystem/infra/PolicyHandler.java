package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Bean
    public Consumer<Message<?>> discardFunction() {
        return message -> {
            // Ingore unnecessary message
            System.out.println("Discarded message: " + message);
        };
    }

    @Bean
    public Consumer<Message<VideoAnalysisFailed>> wheneverVideoAnalysisFailed_UpdateReport() {
        return event -> {
            VideoAnalysisFailed videoAnalysisFailed = event.getPayload();
            Report.updateReport(videoAnalysisFailed);
        };
    }

    @Bean
    public Consumer<Message<IdentifiedAsNonFireEvent>> wheneverIdentifiedAsNonFireEvent_UpdateReport() {
        return event -> {
            IdentifiedAsNonFireEvent identifiedAsNonFireEvent = event.getPayload();
            Report.updateReport(identifiedAsNonFireEvent);
        };
    }

    @Bean
    public Consumer<Message<EventCreated>> wheneverEventCreated_EventIdUpdate() {
        return event -> {
            EventCreated eventCreated = event.getPayload();
            Report.eventIdUpdate(eventCreated);
        };
    }

    @Bean
    public Consumer<Message<AssignedToEvent>> wheneverAssignedToEvent_EventIdUpdate() {
        return event -> {
            AssignedToEvent assignedToEvent = event.getPayload();
            Report.eventIdUpdate(assignedToEvent);
        };
    }

    @Bean
    public Consumer<Message<VideoSaved>> wheneverVideoSaved_VideoIdUpdate() {
        return event -> {
            VideoSaved videoSaved = event.getPayload();
            Report.videoIdUpdate(videoSaved);
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
