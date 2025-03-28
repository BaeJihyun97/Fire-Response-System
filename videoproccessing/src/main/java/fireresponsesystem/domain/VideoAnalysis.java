package fireresponsesystem.domain;

import fireresponsesystem.VideoproccessingApplication;
import fireresponsesystem.domain.FaceBlurred;
import fireresponsesystem.domain.VideoAnalysisFailed;
import fireresponsesystem.domain.VideoAnalyzed;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "VideoAnalysis_table")
@Data
//<<< DDD / Aggregate Root
public class VideoAnalysis {

    @Id
    private String videoAnalysisId;

    private String reportId;

    private String blurredVideoUri;

    private Boolean fireDetected;

    @ElementCollection
    private List<String> tags;

    private String summary;

    @ElementCollection
    private List<String> frames;

    @PostPersist
    public void onPostPersist() {
        VideoAnalyzed videoAnalyzed = new VideoAnalyzed(this);
        videoAnalyzed.publishAfterCommit();

        FaceBlurred faceBlurred = new FaceBlurred(this);
        faceBlurred.publishAfterCommit();

        VideoAnalysisFailed videoAnalysisFailed = new VideoAnalysisFailed(this);
        videoAnalysisFailed.publishAfterCommit();
    }

    public static VideoAnalysisRepository repository() {
        VideoAnalysisRepository videoAnalysisRepository = VideoproccessingApplication.applicationContext.getBean(
            VideoAnalysisRepository.class
        );
        return videoAnalysisRepository;
    }

    //<<< Clean Arch / Port Method
    public static void analyzeVideo(EventIdUpdated eventIdUpdated) {
        //implement business logic here:

        /** Example 1:  new item 
        VideoAnalysis videoAnalysis = new VideoAnalysis();
        repository().save(videoAnalysis);

        VideoAnalyzed videoAnalyzed = new VideoAnalyzed(videoAnalysis);
        videoAnalyzed.publishAfterCommit();
        VideoAnalysisFailed videoAnalysisFailed = new VideoAnalysisFailed(videoAnalysis);
        videoAnalysisFailed.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(eventIdUpdated.get???()).ifPresent(videoAnalysis->{
            
            videoAnalysis // do something
            repository().save(videoAnalysis);

            VideoAnalyzed videoAnalyzed = new VideoAnalyzed(videoAnalysis);
            videoAnalyzed.publishAfterCommit();
            VideoAnalysisFailed videoAnalysisFailed = new VideoAnalysisFailed(videoAnalysis);
            videoAnalysisFailed.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void blurFace(EventIdUpdated eventIdUpdated) {
        //implement business logic here:

        /** Example 1:  new item 
        VideoAnalysis videoAnalysis = new VideoAnalysis();
        repository().save(videoAnalysis);

        FaceBlurred faceBlurred = new FaceBlurred(videoAnalysis);
        faceBlurred.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(eventIdUpdated.get???()).ifPresent(videoAnalysis->{
            
            videoAnalysis // do something
            repository().save(videoAnalysis);

            FaceBlurred faceBlurred = new FaceBlurred(videoAnalysis);
            faceBlurred.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
