package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

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
}
