package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class UpdateEventTypeCommand {

    private String eventId;
    private String eventType;
}
