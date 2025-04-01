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

    private Long postId;
    private String blurredVideoUri;
    private Long userId;
    private Long eventId;
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
