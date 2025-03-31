package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class VideoSaved extends AbstractEvent {

    private Long videoId;
    private Long reportId;
    private String originalVideoUri;
    private String encodedVideoUri;
}