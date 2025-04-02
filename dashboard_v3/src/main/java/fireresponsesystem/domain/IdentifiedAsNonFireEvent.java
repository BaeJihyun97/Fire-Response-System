package fireresponsesystem.domain;

import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class IdentifiedAsNonFireEvent extends AbstractEvent {

    private Long eventId;
    private Float logitude;
    private Float latitude;
    private Boolean verified;
}
