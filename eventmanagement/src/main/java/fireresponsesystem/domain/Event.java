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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long eventId;

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
    public static void createAssignEvent(ReportReceived reportReceived) {
        //implement business logic here:
        // 1. 현재 pending 상태인 모든 이벤트를 찾기

        System.out.println("[POLICY] createAssignEvent");

        List<Event> pendingEvents = repository().findAll()
            .stream()
            .filter(event -> "pending".equals(event.getStatus()))
            .toList();
        
        // 2. reportReceived로부터 위도와 경도 정보 얻기
        double reportLat = reportReceived.getLatitude();
        double reportLon = reportReceived.getLongitude();
        
        // 3. 거리가 5km 이내인 이벤트를 거리 순으로 정렬하여 가져오기
        List<Event> nearbyEvents = pendingEvents.stream()
            .filter(event -> 
                calculateDistance(
                    reportLat, 
                    reportLon, 
                    event.getLatitude(), 
                    event.getLongitude()
                ) <= 5.0)
            .sorted((e1, e2) -> {
                double distance1 = calculateDistance(
                    reportLat, reportLon, e1.getLatitude(), e1.getLongitude());
                double distance2 = calculateDistance(
                    reportLat, reportLon, e2.getLatitude(), e2.getLongitude());
                return Double.compare(distance1, distance2);
            })
            .toList();
            
        if (nearbyEvents.isEmpty()) {
            // 5km 이내에 이벤트가 없는 경우 새 이벤트 생성
            Event newEvent = new Event();
            newEvent.setLatitude((float) reportLat);
            newEvent.setLongitude((float) reportLon);
            newEvent.setStatus("pending");
            newEvent.setCreatedAt(new Date());
            
            repository().save(newEvent);
            
            EventCreated eventCreated = new EventCreated(newEvent);
            eventCreated.setReportId(reportReceived.getReportId());
            eventCreated.publishAfterCommit();

            System.out.println("[POLICY] eventCreated: " + eventCreated.getEventType());
            System.out.println("[POLICY] eventCreated: " + eventCreated.getClass());
            System.out.println("[POLICY] eventCreated: " + eventCreated.getClass().getSimpleName());
        } else {
            // 가장 가까운 이벤트 처리 (정렬된 리스트의 첫 번째 이벤트)
            Event closestEvent = nearbyEvents.get(0);
            
            AssignedToEvent assignedToEvent = new AssignedToEvent(closestEvent);
            assignedToEvent.setReportId(reportReceived.getReportId());
            assignedToEvent.publishAfterCommit();

            System.out.println("[POLICY] assignedToEvent: " + assignedToEvent.getEventType());
            System.out.println("[POLICY] assignedToEvent: " + assignedToEvent.getClass());
            System.out.println("[POLICY] assignedToEvent: " + assignedToEvent.getClass().getSimpleName());
            
        }
    }

    //<<< Clean Arch / Port Method
    // POLICY '화재이벤트로변경' after EVENT'비디오분석완료' (화재일경우만 발행)
    public static void identifyAsFireEvent(VideoAnalyzed videoAnalyzed) {
        //implement business logic here:
        repository().findByEventId(videoAnalyzed.getEventId()).ifPresent(event->{
            
            event.setEventType(videoAnalyzed.getFireDetected()?"fire":"nonFire"); // do something
            repository().save(event);

            if(videoAnalyzed.getFireDetected()) {
                IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(event);
                identifiedAsFireEvent.publishAfterCommit();
            }

        });


    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void identifyAsFireEvent(Reacted reacted) {
        //implement business logic here:        
        repository().findByEventId(reacted.getEventId()).ifPresent(event->{
            
            event.setEventType("fire"); // do something
            repository().save(event);

            IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(event);
            identifiedAsFireEvent.publishAfterCommit();

            });

    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public void updateEventType(UpdateEventTypeCommand updateEventTypeCommand) {
        //implement business logic here:

        repository().findByEventId(updateEventTypeCommand.getEventId()).ifPresent(event->{

            event.setEventType(updateEventTypeCommand.getEventType());
            repository().save(event);

            if(updateEventTypeCommand.getEventType().equals("fire")) {
                IdentifiedAsFireEvent identifiedAsFireEvent = new IdentifiedAsFireEvent(this);
                identifiedAsFireEvent.publishAfterCommit();
            } else {
                IdentifiedAsNonFireEvent identifiedAsNonFireEvent = new IdentifiedAsNonFireEvent(this); 
                identifiedAsNonFireEvent.publishAfterCommit();
            }
        });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void updateStatus(UpdateStatusCommand updateStatusCommand) {
        //implement business logic here:

        repository().findByEventId(updateStatusCommand.getEventId()).ifPresent(event->{

            event.setStatus(updateStatusCommand.getStatus());
            repository().save(event);

            StatusUpdated statusUpdated = new StatusUpdated(this);
            statusUpdated.publishAfterCommit();
        });

        
    }
    //>>> Clean Arch / Port Method



}
//>>> DDD / Aggregate Root
