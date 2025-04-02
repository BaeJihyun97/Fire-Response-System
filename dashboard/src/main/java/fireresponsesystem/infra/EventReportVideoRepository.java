package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "eventReportVideos",
    path = "eventReportVideos"
)
public interface EventReportVideoRepository
    extends PagingAndSortingRepository<EventReportVideo, Long> {
    List<EventReportVideo> findByReportId(Long reportId);
    List<EventReportVideo> findByVideoId(Long videoId);
    List<EventReportVideo> findByEventId(Long eventId);
    List<EventReportVideo> findByPostId(Long postId);
}
