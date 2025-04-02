package fireresponsesystem.domain;

import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ReportUpdated extends AbstractEvent {

    private String reportId;
    private String eventId;
    private String userId;
    private Float longitude;
    private Float latitude;
    private Long videoId;
    private String description;
    private Date uploadedAt;
    private String status;
}
