package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class FaceBlurred extends AbstractEvent {

    private Long videoId;
    private Long reportId;
    private Long eventId;
    private String originalVideoUri;
    private String blurredVideoUri;
    private Boolean fireDetected;
    private Object tags;
    private String summary;
}
