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

    private static final double MAX_DISTANCE_KM = 5.0;
    private static final long MAX_AGE_HOURS = 24;

    @GetMapping("/unread")
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
                if (alarm.getAlarmType() == AlarmType.LOCATION_BASED) {
                    // Check if the alarm is within 5km
                    double distance = LocationUtils.calculateDistance(
                        latitude, longitude,
                        alarm.getLatitude(), alarm.getLongitude()
                    );
                    return distance <= MAX_DISTANCE_KM;
                }
                return true; // Keep non-location-based alarms
            })
            .collect(Collectors.toList());

        return new UserAlarmResponse(alarms);
    }

    @PutMapping("/userAlarms/{userAlarmId}/read")
    public ResponseEntity<?> markAlarmAsRead(@PathVariable Long userAlarmId) {
        return userAlarmRepository.findByUserAlarmId(userAlarmId)
            .map(alarm -> {
                alarm.setIsRead(true);
                userAlarmRepository.save(alarm);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/userAlarms/{userId}")
    public List<UserAlarm> getUserAlarmsByUserId(@PathVariable String userId) {
        return userAlarmRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
//>>> Clean Arch / Inbound Adaptor
