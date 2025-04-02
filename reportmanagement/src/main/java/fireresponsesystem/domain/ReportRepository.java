package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // eventId로 Report 목록을 찾는 메서드
    List<Report> findByEventId(Long eventId);
    
    // userId로 Report 목록을 찾는 메서드
    List<Report> findByUserId(String userId);
}
