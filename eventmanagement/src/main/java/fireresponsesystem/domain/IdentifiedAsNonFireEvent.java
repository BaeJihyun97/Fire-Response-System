package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class IdentifiedAsNonFireEvent extends AbstractEvent {

    private String eventId;
    private Float longitude;
    private Float latitude;
    private boolean verified;

    public IdentifiedAsNonFireEvent(Event aggregate) {
        super(aggregate);
    }

    public IdentifiedAsNonFireEvent() {
        super();
    }
}
//>>> DDD / Domain Event
