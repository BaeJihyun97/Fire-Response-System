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
    public Consumer<Message<?>> routeMessage() {
        return message -> {
            Object rawTypeObj = message.getHeaders().get("type");
            String type = rawTypeObj instanceof byte[] ? new String((byte[]) rawTypeObj, StandardCharsets.UTF_8).trim() : rawTypeObj.toString();
            System.out.println("type: " + type);

            Map<String, Object> payloadMap = (Map<String, Object>) message.getPayload();


            Object tsObj = payloadMap.get("timestamp");
            if(tsObj instanceof String) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                LocalDateTime dateTime = LocalDateTime.parse((String)tsObj, formatter);
                long epochMillis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                payloadMap.put("timestamp", epochMillis);
            }

            switch (type) {
                case "EventCreated":
                    EventCreated eventCreated = objectMapper.convertValue(payloadMap, EventCreated.class);
                    Event.publishPost(eventCreated);
                    break;

                case "Faceblurred":
                    Faceblurred faceblurred = objectMapper.convertValue(payloadMap, Faceblurred.class);
                    Event.updatePost(faceblurred);
                    break;

                case "IdentifiedAsFireEvent":
                    IdentifiedAsFireEvent identifiedAsFireEvent = objectMapper.convertValue(payloadMap, IdentifiedAsFireEvent.class);
                    Event.updatePost(identifiedAsFireEvent);
                    break;

                default:
                    System.out.println("Discarded message: " + message);
                    break;
            }
        };
    }

    @Bean
    public Consumer<Message<?>> discardFunction() {
        return message -> {
            // Ingore unnecessary message
            System.out.println("Discarded message: " + message);
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
