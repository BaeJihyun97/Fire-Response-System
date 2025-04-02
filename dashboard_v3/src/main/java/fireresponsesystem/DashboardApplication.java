package fireresponsesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import java.util.function.Consumer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableFeignClients
public class DashboardApplication {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext =
            SpringApplication.run(DashboardApplication.class, args);
    }
    
    @Bean
    public Consumer<Message<?>> functionRouter() {
        return message -> {
            // 메시지 헤더 및 페이로드를 로깅하면 디버깅에 도움이 됩니다
            System.out.println("Received message: " + message);
            System.out.println("Headers: " + message.getHeaders());
            System.out.println("Payload: " + message.getPayload());
        };
    }
    
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        System.out.println("애플리케이션이 시작되었습니다.");
        System.out.println("Kafka 이벤트 리스너가 등록되었습니다.");
        System.out.println("서비스 명: " + applicationContext.getEnvironment().getProperty("spring.application.name"));
        System.out.println("토픽: fireresponsesystem");
        System.out.println("구독 그룹: dashboard");
    }
}
