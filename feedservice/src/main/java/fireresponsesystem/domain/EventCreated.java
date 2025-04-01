package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class EventCreated extends AbstractEvent {

    private Long eventId;
    private Long userId;
    private Double longitude;
    private Double latitude;
    private String status;
    private String eventType;
    private Date createdAt;
    private Date resolvedAt;
}
