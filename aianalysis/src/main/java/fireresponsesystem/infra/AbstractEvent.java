package fireresponsesystem.infra;

import fireresponsesystem.AianalysisApplication;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;

//<<< Clean Arch / Outbound Adaptor
@Getter
@Setter
public class AbstractEvent {

    String eventType;
    Long timestamp;

    public AbstractEvent(Object aggregate) {
        this();
        BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent() {
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = System.currentTimeMillis();
    }

    public void publish() {
        StreamBridge streamBridge = AianalysisApplication.applicationContext.getBean(
            StreamBridge.class
        );

        streamBridge.send(
            "producer-out-0",
            MessageBuilder
                .withPayload(this)
                .setHeader(
                    MessageHeaders.CONTENT_TYPE,
                    MimeTypeUtils.APPLICATION_JSON
                )
                .setHeader("type", getEventType())
                .build()
        );
    }

    public void publishAfterCommit() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (
                            status ==
                            TransactionSynchronization.STATUS_COMMITTED
                        ) {
                            AbstractEvent.this.publish();
                        }
                    }
                }
            );
        } else {
            // No active transaction, publish immediately
            AbstractEvent.this.publish();
        }
    }

    public boolean validate() {
        return getEventType().equals(getClass().getSimpleName());
    }
}
//>>> Clean Arch / Outbound Adaptor
