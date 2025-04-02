package fireresponsesystem.domain;

import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class Reacted extends AbstractEvent {

    private String postId;
    private Integer reactionCount;
}
