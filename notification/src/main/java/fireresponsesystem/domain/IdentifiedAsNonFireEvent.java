package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentifiedAsNonFireEvent extends AbstractEvent {

    private Long eventId;
    private Float longitude;
    private Float latitude;

}
