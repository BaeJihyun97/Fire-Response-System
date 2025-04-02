package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "userAlarms",
    path = "userAlarms"
)
public interface UserAlarmRepository extends JpaRepository<UserAlarm, Long> {

    List<UserAlarm> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT ua FROM UserAlarm ua WHERE " +
           "(ua.alarmType = 'USER_SPECIFIC' AND ua.userId = :userId AND ua.isRead = false) OR " +
           "(ua.alarmType = 'LOCATION_BASED' AND ua.createdAt >= :startDate AND ua.isRead = false) " +
           "ORDER BY ua.createdAt DESC")
    List<UserAlarm> findUnreadAlarmsWithTimeWindowForLocationBased(
        @Param("userId") String userId,
        @Param("startDate") Date startDate
    );

    @Query("SELECT ua FROM UserAlarm ua WHERE " +
           "(ua.alarmType = 'USER_SPECIFIC' AND ua.userId = :userId) OR " +
           "(ua.alarmType = 'LOCATION_BASED' AND ua.createdAt >= :startDate) " +
           "ORDER BY ua.createdAt DESC")
    List<UserAlarm> findAlarmsWithTimeWindowForLocationBased(
        @Param("userId") String userId,
        @Param("startDate") Date startDate
    );

    Optional<UserAlarm> findByUserAlarmId(Long userAlarmId);
}
