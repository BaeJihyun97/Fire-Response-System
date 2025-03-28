package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class IdentifiedAsFireEvent extends AbstractEvent {

    private String eventId;
    private Float longitude;
    private Float latitude;
    private String status;
    private String eventType;
    private Date createdAt;
    private Date resolvedAt;

    public IdentifiedAsFireEvent(Event aggregate) {
        super(aggregate);
    }

    public IdentifiedAsFireEvent() {
        super();
    }
}
//>>> DDD / Domain Event
