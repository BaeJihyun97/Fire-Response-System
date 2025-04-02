package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public Consumer<Message<?>> discardFunction() {
        return message -> {
            // Ingore unnecessary message
            System.out.println("Discarded message: " + message);
        };
    }

    @Bean
    public Consumer<Message<?>> routeMessage() {
        return message -> {
            Object rawTypeObj = message.getHeaders().get("type");
            String type = rawTypeObj instanceof byte[] ? new String((byte[]) rawTypeObj, StandardCharsets.UTF_8).trim() : rawTypeObj.toString();
            System.out.println("type: " + type);
            if (!"fire".equals(type)  && !"nonFire".equals(type) && !"VideoAnalysisFailed".equals(type)) {
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
                case "fire":
                    IdentifiedAsFireEvent identifiedAsFireEvent = objectMapper.convertValue(payloadMap, IdentifiedAsFireEvent.class);
                    UserAlarm.createUserAlarm(identifiedAsFireEvent);
                    break;

                case "nonFire":
                    IdentifiedAsNonFireEvent identifiedAsNonFireEvent = objectMapper.convertValue(payloadMap, IdentifiedAsNonFireEvent.class);
                    UserAlarm.createUserAlarm(identifiedAsNonFireEvent);
                    break;

                case "VideoAnalysisFailed":
                    VideoAnalysisFailed videoAnalysisFailed = objectMapper.convertValue(payloadMap, VideoAnalysisFailed.class);
                    UserAlarm.createUserAlarm(videoAnalysisFailed);
                    break;

                default:
                    System.out.println("Discarded message: " + message);
                    break;
            }
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
