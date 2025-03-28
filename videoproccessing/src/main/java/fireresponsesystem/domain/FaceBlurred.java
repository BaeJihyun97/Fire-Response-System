package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class FaceBlurred extends AbstractEvent {

    private String videoId;
    private String reportId;
    private String originalVideoUri;
    private String blurredVideoUri;
    private Boolean fireDetected;
    private List<String> tags;
    private String summary;

    public FaceBlurred(VideoAnalysis aggregate) {
        super(aggregate);
    }

    public FaceBlurred() {
        super();
    }
}
//>>> DDD / Domain Event
