package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class RequestAiAnalysisCommand {

    private String analysisId;
    private Rating riskLevel;
    private Money resources;
    private Comment strategy;
}
