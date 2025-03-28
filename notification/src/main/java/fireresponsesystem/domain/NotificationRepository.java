package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "notifications",
    path = "notifications"
)
public interface NotificationRepository
    extends JpaRepository<Notification, Long> {}
