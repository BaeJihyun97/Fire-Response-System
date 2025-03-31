package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            
            Object tsObj = payloadMap.get("timestamp");
            if(tsObj instanceof String) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                LocalDateTime dateTime = LocalDateTime.parse((String)tsObj, formatter);
                long epochMillis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                payloadMap.put("timestamp", epochMillis);
            }

            switch (type) {
                case "VideoAnalysisFailed":
                    System.out.println("VideoAnalysisFailed");
                    VideoAnalysisFailed videoAnalysisFailed = objectMapper.convertValue(message.getPayload(), VideoAnalysisFailed.class);
                    Report.updateReport(videoAnalysisFailed);
                    break;

                case "IdentifiedAsNonFireEvent":
                    System.out.println("IdentifiedAsNonFireEvent");
                    IdentifiedAsNonFireEvent identifiedAsNonFireEvent = objectMapper.convertValue(message.getPayload(), IdentifiedAsNonFireEvent.class);
                    Report.updateReport(identifiedAsNonFireEvent);
                    break;

                case "EventCreated":
                    System.out.println("EventCreated");
                    EventCreated eventCreated = objectMapper.convertValue(message.getPayload(), EventCreated.class);
                    Report.eventIdUpdate(eventCreated);
                    break;

                case "AssignedToEvent":
                    System.out.println("AssignedToEvent");
                    AssignedToEvent assignedToEvent = objectMapper.convertValue(message.getPayload(), AssignedToEvent.class);
                    Report.eventIdUpdate(assignedToEvent);
                    break;

                case "VideoSaved":
                    System.out.println("VideoSaved");
                    VideoSaved videoSaved = objectMapper.convertValue(message.getPayload(), VideoSaved.class);
                    Report.videoIdUpdate(videoSaved);
                    break;

                default:
                    System.out.println("Discarded message");
                    System.out.println("Discarded message: " + message.getHeaders().get("type"));
                    System.out.println("Discarded message: " + message.getHeaders().get("type").getClass());
                    // byte[] rawType = (byte[]) message.getHeaders().get("type");
                    // String decodedType = new String(rawType, StandardCharsets.UTF_8);
                    // System.out.println("Discarded message: " + decodedType);
                    // System.out.println(decodedType.equals("VideoSaved"));
                    System.out.println("Discarded message: " + message.getPayload());
                    System.out.println("Discarded message: " + message.getPayload().getClass());
                    break;
            }

        };
    }

    @Bean
    public Consumer<Message<?>> discardFunction() {
        return message -> {
            // Ingore unnecessary message

            System.out.println("Discarded message");
            System.out.println("Discarded message: " + message.getHeaders().get("type"));
            System.out.println("Discarded message: " + message.getHeaders().get("type").getClass());
            byte[] rawType = (byte[]) message.getHeaders().get("type");
            String decodedType = new String(rawType, StandardCharsets.UTF_8);
            System.out.println("Discarded message: " + decodedType);
            System.out.println(decodedType.equals("VideoSaved"));
            System.out.println("Discarded message: " + message.getPayload());
            System.out.println("Discarded message: " + message.getPayload().getClass());
        };
    }

    // @Bean
    // public Consumer<Message<VideoAnalysisFailed>> wheneverVideoAnalysisFailed_UpdateReport() {
    //     return event -> {
    //         VideoAnalysisFailed videoAnalysisFailed = event.getPayload();
    //         Report.updateReport(videoAnalysisFailed);
    //     };
    // }

    // @Bean
    // public Consumer<Message<IdentifiedAsNonFireEvent>> wheneverIdentifiedAsNonFireEvent_UpdateReport() {
    //     return event -> {
    //         IdentifiedAsNonFireEvent identifiedAsNonFireEvent = event.getPayload();
    //         Report.updateReport(identifiedAsNonFireEvent);
    //     };
    // }

    // @Bean
    // public Consumer<Message<EventCreated>> wheneverEventCreated_EventIdUpdate() {
    //     return event -> {
    //         EventCreated eventCreated = event.getPayload();
    //         Report.eventIdUpdate(eventCreated);
    //     };
    // }

    // @Bean
    // public Consumer<Message<AssignedToEvent>> wheneverAssignedToEvent_EventIdUpdate() {
    //     return event -> {
    //         AssignedToEvent assignedToEvent = event.getPayload();
    //         Report.eventIdUpdate(assignedToEvent);
    //     };
    // }

    // @Bean
    // public Consumer<Message<VideoSaved>> wheneverVideoSaved_VideoIdUpdate() {
    //     System.out.println("wheneverVideoSaved_VideoIdUpdate");
    //     return event -> {
    //         VideoSaved videoSaved = event.getPayload();
    //         Report.videoIdUpdate(videoSaved);
    //     };
    // }
}
//>>> Clean Arch / Inbound Adaptor
