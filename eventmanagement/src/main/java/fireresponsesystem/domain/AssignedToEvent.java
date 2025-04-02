package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;


//<<< DDD / Domain Event
@Data
@ToString
@Getter
@Setter
public class AssignedToEvent extends AbstractEvent {

    private Long eventId;
    private Long reportId;
    private Float longitude;
    private Float latitude;
    private String status;
    private String eventType;
    private String userId;
    private Date createdAt;
    private Date resolvedAt;

    public AssignedToEvent(Event aggregate) {
        super(aggregate);
    }

    public AssignedToEvent() {
        super();
    }
}
//>>> DDD / Domain Event
