package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Consumer<Message<?>> routeMessage() {
        return message -> {
            Object rawTypeObj = message.getHeaders().get("type");
            String type = rawTypeObj instanceof byte[] ? new String((byte[]) rawTypeObj, StandardCharsets.UTF_8).trim() : rawTypeObj.toString();
            System.out.println("type: " + type);
            if (!"EventCreated".equals(type)  && !"FaceBlurred".equals(type) && !"IdentifiedAsFireEvent".equals(type)) {
                return;
            }

            // if payload of the message is in byte, convert it to string
            Map<String, Object> payloadMap;
            if (message.getPayload() instanceof byte[]) {
                String jsonString = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                try {
                    payloadMap = objectMapper.readValue(jsonString, Map.class);
                } catch (JsonProcessingException e) {
                    System.out.println("Failed to parse JSON from byte array: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            } else {
                try {
                    payloadMap = (Map<String, Object>) message.getPayload();
                } catch (Exception e) {
                    System.out.println("Failed to cast payload to Map: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
            System.out.println("Decoded payloadMap: " + payloadMap);
            Object tsObj = payloadMap.get("timestamp");
            if(tsObj instanceof String) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                LocalDateTime dateTime = LocalDateTime.parse((String)tsObj, formatter);
                long epochMillis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                payloadMap.put("timestamp", epochMillis);
            }

            switch (type) {
                case "EventCreated":
                    try {
                        EventCreated eventCreated = objectMapper.convertValue(payloadMap, EventCreated.class);
                        Post.publishPost(eventCreated);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // EventCreated eventCreated = objectMapper.convertValue(payloadMap, EventCreated.class);
                    // Post.publishPost(eventCreated);
                    break;

                // case "FaceBlurred":
                //     FaceBlurred faceBlurred = objectMapper.convertValue(payloadMap, FaceBlurred.class);
                //     Post.updatePost(faceBlurred);
                //     break;

                // case "IdentifiedAsFireEvent":
                //     IdentifiedAsFireEvent identifiedAsFireEvent = objectMapper.convertValue(payloadMap, IdentifiedAsFireEvent.class);
                //     Post.updatePost(identifiedAsFireEvent);
                //     break;

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
