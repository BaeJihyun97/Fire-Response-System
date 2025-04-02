package fireresponsesystem.domain;

import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class PostPublished extends AbstractEvent {

    private String postId;
    private String eventId;
    private Integer reactionCount;
}
