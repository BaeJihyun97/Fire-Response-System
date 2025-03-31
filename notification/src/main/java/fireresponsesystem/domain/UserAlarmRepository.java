package fireresponsesystem.domain;

import fireresponsesystem.domain.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "userAlarms",
    path = "userAlarms"
)
public interface UserAlarmRepository extends JpaRepository<UserAlarm, Long> {

    List<UserAlarm> findByUserId(Long userId);}
