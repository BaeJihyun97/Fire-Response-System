// package fireresponsesystem.infra;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import fireresponsesystem.config.kafka.KafkaProcessor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.stream.annotation.StreamListener;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.stereotype.Component;

// import java.util.HashMap;
// import java.util.Map;

// @Component
// @Slf4j
// public class CustomKafkaListener {

//     @Autowired
//     private ObjectMapper objectMapper;

//     @StreamListener(KafkaProcessor.INPUT)
//     public void handleMessage(@Payload String message) {
//         try {
//             log.info("Received raw message: {}", message);
            
//             // 메시지 유형 확인 및 적절한 처리
//             if (message.contains("FaceBlurred") || message.contains("blurredVideoUri")) {
//                 log.info("Received FaceBlurred event");
//                 // 여기서 필요한 처리 수행
//                 // objectMapper.readValue(message, FaceBlurred.class) 등으로 변환 가능
//             } else if (message.contains("VideoEncoded") || message.contains("encodedVideoUri")) {
//                 log.info("Received VideoEncoded event");
//                 // 여기서 필요한 처리 수행
//             } else if (message.contains("VideoAnalyzed")) {
//                 log.info("Received VideoAnalyzed event");
//                 // 여기서 필요한 처리 수행
//             } else {
//                 log.info("Received unknown event type: {}", message);
//             }
//         } catch (Exception e) {
//             log.error("Error processing message: {}", message, e);
//         }
//     }
// } 