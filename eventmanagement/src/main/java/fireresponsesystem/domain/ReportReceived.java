package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class ReportReceived extends AbstractEvent {

    private Long reportId;
    private Long userId;
    private Float longitude;
    private Float latitude;
    private Long videoId;
    private String description;
    private Date uploadedAt;
    private String status;
}
