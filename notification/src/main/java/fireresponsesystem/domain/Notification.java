package fireresponsesystem.domain;

import fireresponsesystem.NotificationApplication;
import fireresponsesystem.domain.FireEventNotified;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "Notification_table")
@Data
// <<< DDD / Aggregate Root
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long alarmId;

    private Long eventId;

    private Float longitude;

    private Float latitude;

    private Date createdAt;

    private String content;

    @PostPersist
    public void onPostPersist() {
        FireEventNotified fireEventNotified = new FireEventNotified(this);
        fireEventNotified.publishAfterCommit();
    }

    public static NotificationRepository repository() {
        NotificationRepository notificationRepository = NotificationApplication.applicationContext.getBean(
                NotificationRepository.class);
        return notificationRepository;
    }
    // >>> Clean Arch / Port Method

}
// >>> DDD / Aggregate Root
