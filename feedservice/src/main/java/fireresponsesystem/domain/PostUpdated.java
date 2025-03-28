package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PostUpdated extends AbstractEvent {

    private String postId;
    private String blurredVideoUri;
    private String userId;
    private String eventId;
    private List<String> comments;
    private Integer reactionCount;
    private Date createdAt;
    private Date updatedAt;

    public PostUpdated(Post aggregate) {
        super(aggregate);
    }

    public PostUpdated() {
        super();
    }
}
//>>> DDD / Domain Event
