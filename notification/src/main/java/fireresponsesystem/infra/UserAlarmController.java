package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/userAlarms/{userId}")
    public List<UserAlarm> getUserAlarmsByUserId(@PathVariable Long userId) {
        return userAlarmRepository.findByUserId(userId);
    }
}
//>>> Clean Arch / Inbound Adaptor
