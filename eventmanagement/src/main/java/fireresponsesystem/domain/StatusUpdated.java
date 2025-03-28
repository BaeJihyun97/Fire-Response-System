package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class StatusUpdated extends AbstractEvent {

    private String eventId;
    private Float longitude;
    private Float latitude;
    private String status;
    private String eventType;
    private Date createdAt;
    private Date resolvedAt;

    public StatusUpdated(Event aggregate) {
        super(aggregate);
    }

    public StatusUpdated() {
        super();
    }
}
//>>> DDD / Domain Event
