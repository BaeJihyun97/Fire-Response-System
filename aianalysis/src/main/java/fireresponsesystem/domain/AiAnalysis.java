package fireresponsesystem.domain;

import fireresponsesystem.AianalysisApplication;
import fireresponsesystem.domain.AiAnalyzed;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "AiAnalysis_table")
@Data
//<<< DDD / Aggregate Root
public class AiAnalysis {

    @Id
    private String analysisId;

    private String riskLevel;

    private String resources;

    private String response;

    private String eventId;

    private String status;

    @PostPersist
    public void onPostPersist() {
        AiAnalyzed aiAnalyzed = new AiAnalyzed(this);
        aiAnalyzed.publishAfterCommit();
    }

    @PrePersist
    public void onPrePersist() {}

    public static AiAnalysisRepository repository() {
        AiAnalysisRepository aiAnalysisRepository = AianalysisApplication.applicationContext.getBean(
            AiAnalysisRepository.class
        );
        return aiAnalysisRepository;
    }

    //<<< Clean Arch / Port Method
    public static void triggerAiAnalysis(
        IdentifiedAsFireEvent identifiedAsFireEvent
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        AiAnalysis aiAnalysis = new AiAnalysis();
        repository().save(aiAnalysis);

        AiAnalyzed aiAnalyzed = new AiAnalyzed(aiAnalysis);
        aiAnalyzed.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(identifiedAsFireEvent.get???()).ifPresent(aiAnalysis->{
            
            aiAnalysis // do something
            repository().save(aiAnalysis);

            AiAnalyzed aiAnalyzed = new AiAnalyzed(aiAnalysis);
            aiAnalyzed.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
