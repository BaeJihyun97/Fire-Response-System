package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import fireresponsesystem.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AiAnalyzed extends AbstractEvent {

    private String analysisId;
    private String riskLevel;
    private String resources;
    private String strategy;
    private String eventId;

    public AiAnalyzed(AiAnalysis aggregate) {
        super(aggregate);
    }

    public AiAnalyzed() {
        super();
    }
}
//>>> DDD / Domain Event
