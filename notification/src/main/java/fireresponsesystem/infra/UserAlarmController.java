package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/userAlarms")
@Transactional
public class UserAlarmController {

    @Autowired
    UserAlarmRepository userAlarmRepository;

    @Autowired
    QueriedAlarmRepository queriedAlarmRepository;

    private static final double MAX_DISTANCE_KM = 5.0;
    private static final long MAX_AGE_HOURS = 24;

    @GetMapping("/notification/unread")
    public UserAlarmResponse getUnreadAlarmByUserId(
            @RequestParam String userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        // Calculate the start date for 24-hour window (only used for LOCATION_BASED alarms)
        Date startDate = new Date(System.currentTimeMillis() - (MAX_AGE_HOURS * 60 * 60 * 1000));

        // Get all unread alarms
        List<UserAlarm> alarms = userAlarmRepository.findUnreadAlarmsWithTimeWindowForLocationBased(userId, startDate);
        // Filter alarms based on location
        alarms = alarms.stream()
            .filter(alarm -> {
                System.out.println(alarm);
                if (alarm.getAlarmType() == AlarmType.LOCATION_BASED && alarm.getLatitude() != null && alarm.getLongitude() != null) {
                    // Check if the alarm is within 5km

                    double distance = LocationUtils.calculateDistance(
                        latitude, longitude,
                        alarm.getLatitude(), alarm.getLongitude()
                    );
                    System.out.println(distance);
                    return distance <= MAX_DISTANCE_KM;
                }
                return true; // Keep non-location-based alarms
            })
            .collect(Collectors.toList());

        // Create queried alarms for each alarm that was retrieved
        alarms.forEach(alarm -> {
            QueriedAlarm.createQueriedAlarm(alarm, userId);
        });

        return new UserAlarmResponse(alarms);
    }

    @PutMapping("/notification/{userAlarmId}/read")
    public ResponseEntity<?> markAlarmAsRead(@PathVariable Long userAlarmId) {
        return userAlarmRepository.findByUserAlarmId(userAlarmId)
            .map(alarm -> {
                alarm.setIsRead(true);
                userAlarmRepository.save(alarm);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }



//>>> Clean Arch / Inbound Adaptor
    @GetMapping("/notification/{userId}")
    public UserAlarmResponse getUserAlarmsByUserId(
            @PathVariable String userId) {

        // Get all queried alarms for this user, ordered by query time descending
        List<QueriedAlarm> queriedAlarms = queriedAlarmRepository.findByQueriedByOrderByQueriedAtDesc(userId);

        // Extract the UserAlarms from the QueriedAlarms
        List<UserAlarm> alarms = queriedAlarms.stream()
            .map(QueriedAlarm::getUserAlarm)
            .collect(Collectors.toList());

        return new UserAlarmResponse(alarms);
    }
}