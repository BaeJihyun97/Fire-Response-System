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

    private Long id;

    public IdentifiedAsNonFireEvent(Event aggregate) {
        super(aggregate);
    }

    public IdentifiedAsNonFireEvent() {
        super();
    }
}
//>>> DDD / Domain Event
