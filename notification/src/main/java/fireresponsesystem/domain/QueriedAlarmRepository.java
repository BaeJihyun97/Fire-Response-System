package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "queriedAlarms",
    path = "queriedAlarms"
)
public interface QueriedAlarmRepository extends JpaRepository<QueriedAlarm, Long> {
    List<QueriedAlarm> findByUserAlarmOrderByQueriedAtDesc(UserAlarm userAlarm);
    List<QueriedAlarm> findByQueriedByOrderByQueriedAtDesc(String queriedBy);
    boolean existsByUserAlarm(UserAlarm userAlarm);
    boolean existsByUserAlarmAndQueriedBy(UserAlarm userAlarm, String queriedBy);
}
//>>> PoEAA / Repository