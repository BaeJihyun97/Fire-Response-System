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
    public Consumer<Message<IdentifiedAsFireEvent>> wheneverIdentifiedAsFireEvent_NotifyFireEvent() {
        return event -> {
            IdentifiedAsFireEvent identifiedAsFireEvent = event.getPayload();
            Notification.notifyFireEvent(identifiedAsFireEvent);
        };
    }

    @Bean
    public Consumer<Message<StatusUpdated>> wheneverStatusUpdated_NotifyFireEventUpdated() {
        return event -> {
            StatusUpdated statusUpdated = event.getPayload();
            Notification.notifyFireEventUpdated(statusUpdated);
        };
    }

    @Bean
    public Consumer<Message<VideoAnalysisFailed>> wheneverVideoAnalysisFailed_NotifyVideoAnalysisFailed() {
        return event -> {
            VideoAnalysisFailed videoAnalysisFailed = event.getPayload();
            UserAlarm.notifyVideoAnalysisFailed(videoAnalysisFailed);
        };
    }

    @Bean
    public Consumer<Message<FireEventNotified>> wheneverFireEventNotified_NotifyByLocation() {
        return event -> {
            FireEventNotified fireEventNotified = event.getPayload();
            UserAlarm.notifyByLocation(fireEventNotified);
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
