package fireresponsesystem.infra;

import fireresponsesystem.domain.UserAlarm;
import java.util.List;
import lombok.Data;

@Data
public class UserAlarmResponse {
    private List<UserAlarm> alarms;
    private long count;

    public UserAlarmResponse(List<UserAlarm> alarms) {
        this.alarms = alarms;
        this.count = alarms.size();
    }
}