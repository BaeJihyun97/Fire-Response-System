package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class FireEventNotified extends AbstractEvent {

    private Long alarmId;
    private Long eventId;
    private Float longitude;
    private Float latitude;
    private Date createdAt;
    private String content;

    public FireEventNotified(Notification aggregate) {
        super(aggregate);
    }

    public FireEventNotified() {
        super();
    }
}
//>>> DDD / Domain Event
