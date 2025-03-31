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

    // <<< Clean Arch / Port Method
    public static void notifyFireEvent(
            IdentifiedAsFireEvent identifiedAsFireEvent) {
        // 새로운 Notification 객체 생성
        Notification notification = new Notification();
        notification.setEventId(identifiedAsFireEvent.getEventId());
        notification.setLongitude(identifiedAsFireEvent.getLongitude());
        notification.setLatitude(identifiedAsFireEvent.getLatitude());
        notification.setCreatedAt(identifiedAsFireEvent.getCreatedAt());

        // eventTime을 "xx시 xx분" 형식으로 변환
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH시 mm분");
        String formattedTime = timeFormat.format(notification.getCreatedAt());

        notification.setContent("화재가 " + formattedTime + "에 발생하였습니다.");

        // DB에 저장
        repository().save(notification);
    }

    // >>> Clean Arch / Port Method
    // <<< Clean Arch / Port Method
    public static void notifyFireEventUpdated(StatusUpdated statusUpdated) {
        // 새로운 Notification 객체 생성
        Notification notification = new Notification();
        notification.setEventId(statusUpdated.getEventId());
        notification.setLongitude(statusUpdated.getLongitude());
        notification.setLatitude(statusUpdated.getLatitude());
        notification.setCreatedAt(statusUpdated.getCreatedAt());

        // eventTime을 "xx시 xx분" 형식으로 변환
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH시 mm분");
        String formattedTime = timeFormat.format(notification.getCreatedAt());

        // content 설정
        notification.setContent( formattedTime + "에 발생한 화재가 진화되었습니다.");

        // DB에 저장
        repository().save(notification);
    }
    // >>> Clean Arch / Port Method

}
// >>> DDD / Aggregate Root
