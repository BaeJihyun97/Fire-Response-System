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
    public Consumer<Message<EventCreated>> wheneverEventCreated_PublishPost() {
        return event -> {
            EventCreated eventCreated = event.getPayload();
            Post.publishPost(eventCreated);
        };
    }

    @Bean
    public Consumer<Message<FaceBlurred>> wheneverFaceBlurred_PostUpdated() {
        return event -> {
            FaceBlurred faceBlurred = event.getPayload();
            Post.postUpdated(faceBlurred);
        };
    }

    @Bean
    public Consumer<Message<IdentifiedAsFireEvent>> wheneverIdentifiedAsFireEvent_PostUpdated() {
        return event -> {
            IdentifiedAsFireEvent identifiedAsFireEvent = event.getPayload();
            Post.postUpdated(identifiedAsFireEvent);
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
