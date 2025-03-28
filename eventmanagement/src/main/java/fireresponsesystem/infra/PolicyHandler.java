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
    public Consumer<Message<ReportReceived>> wheneverReportReceived_CreateEvent() {
        return event -> {
            ReportReceived reportReceived = event.getPayload();
            Event.createEvent(reportReceived);
        };
    }

    @Bean
    public Consumer<Message<ReportReceived>> wheneverReportReceived_AssignToEvent() {
        return event -> {
            ReportReceived reportReceived = event.getPayload();
            Event.assignToEvent(reportReceived);
        };
    }

    @Bean
    public Consumer<Message<VideoAnalyzed>> wheneverVideoAnalyzed_IdentifyAsFireEvent() {
        return event -> {
            VideoAnalyzed videoAnalyzed = event.getPayload();
            Event.identifyAsFireEvent(videoAnalyzed);
        };
    }

    @Bean
    public Consumer<Message<Reacted>> wheneverReacted_IdentifyAsFireEvent() {
        return event -> {
            Reacted reacted = event.getPayload();
            Event.identifyAsFireEvent(reacted);
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
