package fireresponsesystem.domain;

import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class VideoAnalyzed extends AbstractEvent {

    private Long videoAnalysisId;
    private Long videoId;
    private Long reportId;
    private Long eventId;
    private Boolean success;
    private Boolean fireDetected;
    private List<String> tags;
    private String severity;
    private String analyzedData;
    private Long timestamp;
}
