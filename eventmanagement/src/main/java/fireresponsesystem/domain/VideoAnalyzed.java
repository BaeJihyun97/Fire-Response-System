package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class VideoAnalyzed extends AbstractEvent {

    private Long videoAnalysisId;
    private Boolean fireDetected;
    private String summary;
    private String status; // completed, failed
    private List<String> frameUris; 
    private Long reportId;
    private Long eventId;
}
