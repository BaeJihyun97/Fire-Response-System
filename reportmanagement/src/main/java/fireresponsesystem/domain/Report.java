package fireresponsesystem.domain;

import fireresponsesystem.ReportmanagementApplication;
import fireresponsesystem.domain.EventIdUpdated;
import fireresponsesystem.domain.ReportReceived;
import fireresponsesystem.domain.ReportUpdated;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "Report_table")
@Data
//<<< DDD / Aggregate Root
public class Report {

    @Id
    private String reportId;

    private String eventId;

    private String userId;

    private Float longitude;

    private Float latitude;

    private String videoUri;

    private String description;

    private Date uploadedAt;

    private String status;

    @PostPersist
    public void onPostPersist() {
        ReportReceived reportReceived = new ReportReceived(this);
        reportReceived.publishAfterCommit();

        ReportUpdated reportUpdated = new ReportUpdated(this);
        reportUpdated.publishAfterCommit();

        EventIdUpdated eventIdUpdated = new EventIdUpdated(this);
        eventIdUpdated.publishAfterCommit();
    }

    @PrePersist
    public void onPrePersist() {}

    public static ReportRepository repository() {
        ReportRepository reportRepository = ReportmanagementApplication.applicationContext.getBean(
            ReportRepository.class
        );
        return reportRepository;
    }

    //<<< Clean Arch / Port Method
    public static void updateReport(VideoAnalysisFailed videoAnalysisFailed) {
        //implement business logic here:

        /** Example 1:  new item 
        Report report = new Report();
        repository().save(report);

        ReportUpdated reportUpdated = new ReportUpdated(report);
        reportUpdated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(videoAnalysisFailed.get???()).ifPresent(report->{
            
            report // do something
            repository().save(report);

            ReportUpdated reportUpdated = new ReportUpdated(report);
            reportUpdated.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateReport(
        IdentifiedAsNonFireEvent identifiedAsNonFireEvent
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Report report = new Report();
        repository().save(report);

        ReportUpdated reportUpdated = new ReportUpdated(report);
        reportUpdated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(identifiedAsNonFireEvent.get???()).ifPresent(report->{
            
            report // do something
            repository().save(report);

            ReportUpdated reportUpdated = new ReportUpdated(report);
            reportUpdated.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void eventIdUpdate(EventCreated eventCreated) {
        //implement business logic here:

        /** Example 1:  new item 
        Report report = new Report();
        repository().save(report);

        EventIdUpdated eventIdUpdated = new EventIdUpdated(report);
        eventIdUpdated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(eventCreated.get???()).ifPresent(report->{
            
            report // do something
            repository().save(report);

            EventIdUpdated eventIdUpdated = new EventIdUpdated(report);
            eventIdUpdated.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void eventIdUpdate(AssignedToEvent assignedToEvent) {
        //implement business logic here:

        /** Example 1:  new item 
        Report report = new Report();
        repository().save(report);

        EventIdUpdated eventIdUpdated = new EventIdUpdated(report);
        eventIdUpdated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(assignedToEvent.get???()).ifPresent(report->{
            
            report // do something
            repository().save(report);

            EventIdUpdated eventIdUpdated = new EventIdUpdated(report);
            eventIdUpdated.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
