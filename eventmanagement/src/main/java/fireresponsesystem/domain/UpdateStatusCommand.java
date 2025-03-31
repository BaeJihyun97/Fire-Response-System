package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class UpdateStatusCommand {

    private Long eventId;
    private String status;
}
