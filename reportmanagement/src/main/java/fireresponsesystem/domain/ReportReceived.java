package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ReportReceived extends AbstractEvent {

    private Long reportId;
    private String userId;
    private Float longitude;
    private Float latitude;
    private Long videoId;
    private String description;
    private Date uploadedAt;
    private String status;

    public ReportReceived(Report aggregate) {
        super(aggregate);
    }

    public ReportReceived() {
        super();
    }
}
//>>> DDD / Domain Event
