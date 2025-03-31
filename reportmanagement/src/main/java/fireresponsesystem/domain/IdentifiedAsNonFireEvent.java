package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class IdentifiedAsNonFireEvent extends AbstractEvent {

    private Long reportId;
}
