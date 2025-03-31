package fireresponsesystem.domain;

import fireresponsesystem.ReportmanagementApplication;
import fireresponsesystem.domain.EventVideoIdUpdated;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reportId;

    private Long eventId;

    private Long userId;

    private Float longitude;

    private Float latitude;

    private Long videoId;

    private String description;

    private Date uploadedAt;

    private String status; // pending, success, failed, fire, nonfire

    @PostPersist
    public void onPostPersist() {
        // ReportUpdated reportUpdated = new ReportUpdated(this);
        // reportUpdated.publishAfterCommit();

        // EventVideoIdUpdated eventVideoIdUpdated = new EventVideoIdUpdated(this);
        // eventVideoIdUpdated.publishAfterCommit();
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
    // COMMAND'신고받음'
    public void receiveReport(ReceiveReportCommand receiveReportCommand) {
        //implement business logic here:

        this.userId = receiveReportCommand.getUserId();
        this.longitude = receiveReportCommand.getLongitude();
        this.latitude = receiveReportCommand.getLatitude();
        this.description = receiveReportCommand.getDescription();

        this.status = "pending";
        this.uploadedAt = new Date();
        this.videoId = null;
        this.eventId = null;

        repository().save(this);

        ReportReceived reportReceived = new ReportReceived(this);
        reportReceived.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    // POLICY'신고업데이트' after EVENT'영상분석실패함'
    public static void updateReport(VideoAnalysisFailed videoAnalysisFailed) {
        //implement business logic here:
        repository().findById(videoAnalysisFailed.getReportId()).ifPresent(report->{
            
            report.status = "failed"; // do something
            repository().save(report);

            // ReportUpdated reportUpdated = new ReportUpdated(report);
            // reportUpdated.publishAfterCommit();
         });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    // POLICY'신고업데이트' after EVENT'비화제이벤트로변경됨'
    public static void updateReport(
        IdentifiedAsNonFireEvent identifiedAsNonFireEvent
    ) {
        //implement business logic here:
        List<Report> reports = repository().findByEventId(identifiedAsNonFireEvent.getReportId());
        for (Report report : reports) {
            report.status = "fire";
            repository().save(report);

            // ReportUpdated reportUpdated = new ReportUpdated(report);
            // reportUpdated.publishAfterCommit();
        }

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    // POLICY'이벤트ID업데이트' after EVENT'이벤트생성됨'
    public static void eventIdUpdate(EventCreated eventCreated) {
        //implement business logic here:
        repository().findById(eventCreated.getReportId()).ifPresent(report->{
            
            report.eventId = eventCreated.getEventId(); // do something
            repository().save(report);

            if(report.getEventId() != null && report.getVideoId() != null){
                EventVideoIdUpdated eventVideoIdUpdated = new EventVideoIdUpdated(report);
                eventVideoIdUpdated.publishAfterCommit();
            }
         });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    // POLICY'이벤트ID업데이트' after EVENT'이벤트할당됨'
    public static void eventIdUpdate(AssignedToEvent assignedToEvent) {
        //implement business logic here:
        repository().findById(assignedToEvent.getReportId()).ifPresent(report->{

            report.status = "pending";
            repository().save(report);

            EventVideoIdUpdated eventVideoIdUpdated = new EventVideoIdUpdated(report);
            eventVideoIdUpdated.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    // POLICY'영상ID업데이트' after EVENT'영상저장됨'
    public static void videoIdUpdate(VideoSaved videoSaved) {
        //implement business logic here:
        System.out.println("videoIdUpdate: " + videoSaved.getVideoId());
        repository().findById(videoSaved.getReportId()).ifPresent(report->{
            
            report.videoId = videoSaved.getVideoId(); // do something
            repository().save(report);

            if(report.getEventId() != null && report.getVideoId() != null){
                EventVideoIdUpdated eventVideoIdUpdated = new EventVideoIdUpdated(report);
                eventVideoIdUpdated.publishAfterCommit();
            }
        });
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
