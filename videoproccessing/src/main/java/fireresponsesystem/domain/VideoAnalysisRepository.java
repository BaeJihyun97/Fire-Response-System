package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "videoAnalyses",
    path = "videoAnalyses"
)
public interface VideoAnalysisRepository
    extends JpaRepository<VideoAnalysis, String> {}
