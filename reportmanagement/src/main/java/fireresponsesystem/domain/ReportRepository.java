package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository extends JpaRepository<Report, String> {}
