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
public class EventCreated extends AbstractEvent {

    private Long eventId;
    private Float longitude;
    private Float latitude;
    private String status;
    private String eventType;
    private Date createdAt;
    private Date resolvedAt;
    private Long reportId;

    public EventCreated(Event aggregate) {
        super(aggregate);
    }

    public EventCreated() {
        super();
    }
}
//>>> DDD / Domain Event
