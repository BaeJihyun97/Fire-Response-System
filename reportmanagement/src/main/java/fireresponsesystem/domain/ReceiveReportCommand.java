package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ReceiveReportCommand {

    private Long userId;
    private Float longitude;
    private Float latitude;
    private String description;
}