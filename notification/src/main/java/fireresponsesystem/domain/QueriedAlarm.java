package fireresponsesystem.domain;

import fireresponsesystem.NotificationApplication;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "QueriedAlarm_table", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userAlarmId", "queriedBy"})
})
@Data
//<<< DDD / Aggregate Root
public class QueriedAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userAlarmId", nullable = false)
    private UserAlarm userAlarm;

    @CreationTimestamp
    private LocalDateTime queriedAt;

    private String queriedBy;

    public static QueriedAlarmRepository repository() {
        QueriedAlarmRepository queriedAlarmRepository = NotificationApplication.applicationContext.getBean(
            QueriedAlarmRepository.class
        );
        return queriedAlarmRepository;
    }

    //<<< Clean Arch / Port Method
    public static void createQueriedAlarm(UserAlarm userAlarm, String queriedBy) {
        // Check if a record already exists for this user and alarm
        if (repository().existsByUserAlarmAndQueriedBy(userAlarm, queriedBy)) {
            return; // Skip creating duplicate record
        }

        QueriedAlarm queriedAlarm = new QueriedAlarm();
        queriedAlarm.setUserAlarm(userAlarm);
        queriedAlarm.setQueriedBy(queriedBy);

        // DB에 저장
        repository().save(queriedAlarm);
    }
}
//>>> DDD / Aggregate Root