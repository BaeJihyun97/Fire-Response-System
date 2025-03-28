package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class Reacted extends AbstractEvent {

    private String postId;
    private String blurredVideoUri;
    private String userId;
    private String eventId;
    private Object comments;
    private Integer reactionCount;
    private Date createdAt;
    private Date updatedAt;
}
