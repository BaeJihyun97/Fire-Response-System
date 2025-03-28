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

    private String status;

    private String eventType;

    private Date createdAt;

    private Date resolvedAt;

    @PostPersist
    public void onPostPersist() {
        EventCreated eventCreated = new EventCreated(this);
        eventCreated.publishAfterCommit();

        AssignedToEvent assignedToEvent = new AssignedToEvent(this);
        assignedToEvent.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate() {}

    public static EventRepository repository() {
        EventRepository eventRepository = EventmanagementApplication.applicationContext.getBean(
            EventRepository.class
        );
        return eventRepository;
    }

    //<<< Clean Arch / Port Method
    public void updateEventType(UpdateEventTypeCommand updateEventTypeCommand) {
        //implement business logic here:

        IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(
            this
        );
        identifiedAsFireEvent.publishAfterCommit();

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

    //<<< Clean Arch / Port Method
    public static void createEvent(ReportReceived reportReceived) {
        //implement business logic here:

        /** Example 1:  new item 
        Event event = new Event();
        repository().save(event);

        EventCreated eventCreated = new EventCreated(event);
        eventCreated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(reportReceived.get???()).ifPresent(event->{
            
            event // do something
            repository().save(event);

            EventCreated eventCreated = new EventCreated(event);
            eventCreated.publishAfterCommit();

         });
        */

    }

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
