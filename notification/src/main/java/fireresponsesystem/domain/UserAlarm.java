package fireresponsesystem.domain;

import fireresponsesystem.NotificationApplication;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "UserAlarm_table")
@Data
//<<< DDD / Aggregate Root
public class UserAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userAlarmId;

    private Long userId;

    private Long alarmId;

    private Long eventId;

    private Float longitude;

    private Float latitude;

    private Date createdAt;

    private Long reportId;

    private String content;

    @Autowired
    private static UserRepository userRepository;

    public static UserAlarmRepository repository() {
        UserAlarmRepository userAlarmRepository = NotificationApplication.applicationContext.getBean(
            UserAlarmRepository.class
        );
        return userAlarmRepository;
    }

    //<<< Clean Arch / Port Method
    public static void notifyVideoAnalysisFailed(
        VideoAnalysisFailed videoAnalysisFailed
    ) {
        // 모든 사용자에 대해 알람 생성
        List<Long> userIds = getAllUserIds(); // 모든 사용자 ID를 가져오는 메서드

        for (Long userId : userIds) {
            UserAlarm userAlarm = new UserAlarm();
            userAlarm.setUserId(userId);
            userAlarm.setReportId(videoAnalysisFailed.getReportId());
            userAlarm.setContent("업로드한 영상 분석을 실패하였습니다.");

            // DB에 저장
            repository().save(userAlarm);
        }
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void notifyByLocation(FireEventNotified fireEventNotified) {
        // 모든 사용자에 대해 알람 생성
        List<Long> userIds = getAllUserIds(); // 모든 사용자 ID를 가져오는 메서드

        for (Long userId : userIds) {
            UserAlarm userAlarm = new UserAlarm();
            userAlarm.setUserId(userId);
            userAlarm.setAlarmId(fireEventNotified.getAlarmId());
            userAlarm.setEventId(fireEventNotified.getEventId());
            userAlarm.setLongitude(fireEventNotified.getLongitude());
            userAlarm.setLatitude(fireEventNotified.getLatitude());
            userAlarm.setCreatedAt(fireEventNotified.getCreatedAt());
            userAlarm.setContent(fireEventNotified.getContent());

            // DB에 저장
            repository().save(userAlarm);
        }
    }
    //>>> Clean Arch / Port Method

    // 모든 사용자 ID를 가져오는 메서드
    private static List<Long> getAllUserIds() {
        // UserRepository를 사용하여 모든 사용자 ID를 가져옵니다.
        return userRepository.findAllUserIds();
    }
    
}
//>>> DDD / Aggregate Root
