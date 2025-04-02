package fireresponsesystem.domain;

import fireresponsesystem.NotificationApplication;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "UserAlarm_table")
@Data
//<<< DDD / Aggregate Root
public class UserAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userAlarmId;

    @Column(nullable = true)
    private String userId;

    private Long eventId;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Column(nullable = true)
    private Float longitude;

    @Column(nullable = true)
    private Float latitude;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(nullable = true)
    private Long reportId;

    private String content;

    private Boolean isRead;

    @Column(nullable = true)
    private String videoId;

    public static UserAlarmRepository repository() {
        UserAlarmRepository userAlarmRepository = NotificationApplication.applicationContext.getBean(
            UserAlarmRepository.class
        );
        return userAlarmRepository;
    }

    //<<< Clean Arch / Port Method
    public static void createUserAlarm(
        IdentifiedAsFireEvent identifiedAsFireEvent
    ) {
        UserAlarm userAlarm = new UserAlarm();
        userAlarm.setEventId(identifiedAsFireEvent.getEventId());
        userAlarm.setLongitude(identifiedAsFireEvent.getLongitude());
        userAlarm.setLatitude(identifiedAsFireEvent.getLatitude());
        userAlarm.setContent("현재 위치 5km 내 화재가 식별되었습니다.");
        userAlarm.setAlarmType(AlarmType.LOCATION_BASED);
        userAlarm.setIsRead(false);

        // DB에 저장
        repository().save(userAlarm);
    }

    public static void createUserAlarm(
        IdentifiedAsNonFireEvent identifiedAsNonFireEvent
    ) {
        UserAlarm userAlarm = new UserAlarm();
        userAlarm.setEventId(identifiedAsNonFireEvent.getEventId());
        userAlarm.setLongitude(identifiedAsNonFireEvent.getLongitude());
        userAlarm.setLatitude(identifiedAsNonFireEvent.getLatitude());
        userAlarm.setContent("현재 위치 5km 내 화재가 종결되었습니다.");
        userAlarm.setAlarmType(AlarmType.LOCATION_BASED);
        userAlarm.setIsRead(false);

        // DB에 저장
        repository().save(userAlarm);
    }

    public static void createUserAlarm(
        VideoAnalysisFailed videoAnalysisFailed
    ) {
        UserAlarm userAlarm = new UserAlarm();
        userAlarm.setEventId(videoAnalysisFailed.getEventId());
        userAlarm.setVideoId(videoAnalysisFailed.getVideoId());
        userAlarm.setContent("업로드한 영상 분석을 실패하였습니다. 다시 업로드해주세요.");
        userAlarm.setAlarmType(AlarmType.USER_SPECIFIC);
        userAlarm.setIsRead(false);

        // DB에 저장
        repository().save(userAlarm);
    }
}
//>>> DDD / Aggregate Root
