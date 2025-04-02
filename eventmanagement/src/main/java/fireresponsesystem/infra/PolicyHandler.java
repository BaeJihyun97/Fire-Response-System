package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;


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

            Map<String, Object> payloadMap = (Map<String, Object>) message.getPayload();

            // ObjectMapper 설정 추가
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            
            Object tsObj = payloadMap.get("timestamp");
            if(tsObj instanceof String) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                LocalDateTime dateTime = LocalDateTime.parse((String)tsObj, formatter);
                long epochMillis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                payloadMap.put("timestamp", epochMillis);
            }

            switch (type) {
                case "ReportReceived": // wheneverReportReceived_CreateEvent
                    ReportReceived reportReceived = objectMapper.convertValue(payloadMap, ReportReceived.class);
                    Event.createAssignEvent(reportReceived);
                    break;

                case "VideoAnalyzed": // wheneverVideoAnalyzed_IdentifyAsFireEvent
                    VideoAnalyzed videoAnalyzed = objectMapper.convertValue(payloadMap, VideoAnalyzed.class);
                    Event.identifyAsFireEvent(videoAnalyzed);
                    break;
                
                case "Reacted": // wheneverReacted_IdentifyAsFireEvent
                    Reacted reacted = objectMapper.convertValue(payloadMap, Reacted.class);
                    Event.identifyAsFireEvent(reacted);
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
