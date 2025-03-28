package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ReceiveReportCommand {

    private String reportId;
    private Address location;
    private File video;
    private Comment description;
}
