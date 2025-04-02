package fireresponsesystem.domain;

import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class VideoAnalysisFailed extends AbstractEvent {

    private String blurredVideoUri;
    private Long eventId;
    private Long videoId;
}
