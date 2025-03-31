package fireresponsesystem.domain;

import fireresponsesystem.EventmanagementApplication;
import fireresponsesystem.domain.AssignedToEvent;
import fireresponsesystem.domain.EventCreated;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "Event_table")
@Data
//<<< DDD / Aggregate Root
public class Event {

    @Id
    private String eventId;

    private Float longitude;

    private Float latitude;

    private String status; // pending, resolved

    private String eventType; // fire, nonFire

    private Date createdAt;

    private Date resolvedAt;

    @PostPersist
    public void onPostPersist() {
        // EventCreated eventCreated = new EventCreated(this);
        // eventCreated.publishAfterCommit();

        // AssignedToEvent assignedToEvent = new AssignedToEvent(this);
        // assignedToEvent.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate() {}

    public static EventRepository repository() {
        EventRepository eventRepository = EventmanagementApplication.applicationContext.getBean(
            EventRepository.class
        );
        return eventRepository;
    }

    // 두 지점 간의 거리 계산 (Haversine 공식 사용)
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (킬로미터)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 결과 거리 (킬로미터)
    }

    //<<< Clean Arch / Port Method
    // POLICY'이벤트생성' after EVENT'신고받음'
    public static void createEvent(ReportReceived reportReceived) {
        //implement business logic here:
        repository().findById(reportReceived.get???()).ifPresent(event->{
            
            event // do something
            repository().save(event);

            EventCreated eventCreated = new EventCreated(event);
            eventCreated.publishAfterCommit();

        });
    }

    //<<< Clean Arch / Port Method
    public void updateEventType(UpdateEventTypeCommand updateEventTypeCommand) {
        //implement business logic here:

        IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(
            this
        );
        identifiedAsFireEvent.publishAfterCommit();

        IdentifiedAsNonFireEvent identifiedAsNonFireEvent = new IdentifiedAsNonFireEvent(
            this
        );
        identifiedAsNonFireEvent.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void updateStatus(UpdateStatusCommand updateStatusCommand) {
        //implement business logic here:

        StatusUpdated statusUpdated = new StatusUpdated(this);
        statusUpdated.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method



    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void assignToEvent(ReportReceived reportReceived) {
        //implement business logic here:

        /** Example 1:  new item 
        Event event = new Event();
        repository().save(event);

        AssignedToEvent assignedToEvent = new AssignedToEvent(event);
        assignedToEvent.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(reportReceived.get???()).ifPresent(event->{
            
            event // do something
            repository().save(event);

            AssignedToEvent assignedToEvent = new AssignedToEvent(event);
            assignedToEvent.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void identifyAsFireEvent(VideoAnalyzed videoAnalyzed) {
        //implement business logic here:

        /** Example 1:  new item 
        Event event = new Event();
        repository().save(event);

        IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(event);
        identifiedAsFireEvent.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(videoAnalyzed.get???()).ifPresent(event->{
            
            event // do something
            repository().save(event);

            IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(event);
            identifiedAsFireEvent.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void identifyAsFireEvent(Reacted reacted) {
        //implement business logic here:

        /** Example 1:  new item 
        Event event = new Event();
        repository().save(event);

        IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(event);
        identifiedAsFireEvent.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(reacted.get???()).ifPresent(event->{
            
            event // do something
            repository().save(event);

            IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(event);
            identifiedAsFireEvent.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
