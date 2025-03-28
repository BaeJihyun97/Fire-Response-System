package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ReportUpdated extends AbstractEvent {

    private String reportId;
    private String eventId;
    private String userId;
    private Float longitude;
    private Float latitude;
    private String videoUri;
    private String description;
    private Date uploadedAt;
    private String status;

    public ReportUpdated(Report aggregate) {
        super(aggregate);
    }

    public ReportUpdated() {
        super();
    }
}
//>>> DDD / Domain Event
