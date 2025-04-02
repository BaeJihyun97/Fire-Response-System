package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ReceiveReportCommand {

    private String userId;
    private Float longitude;
    private Float latitude;
    private String description;
}