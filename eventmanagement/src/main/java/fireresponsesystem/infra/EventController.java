package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/events")
@Transactional
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @RequestMapping(
        value = "events/{id}/updateeventtype",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Event updateEventType(
        @PathVariable(value = "id") String id,
        @RequestBody UpdateEventTypeCommand updateEventTypeCommand,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /event/updateEventType  called #####");
        Optional<Event> optionalEvent = eventRepository.findById(id);

        optionalEvent.orElseThrow(() -> new Exception("No Entity Found"));
        Event event = optionalEvent.get();
        event.updateEventType(updateEventTypeCommand);

        eventRepository.save(event);
        return event;
    }

    @RequestMapping(
        value = "events/{id}/updatestatus",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Event updateStatus(
        @PathVariable(value = "id") String id,
        @RequestBody UpdateStatusCommand updateStatusCommand,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /event/updateStatus  called #####");
        Optional<Event> optionalEvent = eventRepository.findById(id);

        optionalEvent.orElseThrow(() -> new Exception("No Entity Found"));
        Event event = optionalEvent.get();
        event.updateStatus(updateStatusCommand);

        eventRepository.save(event);
        return event;
    }
}
//>>> Clean Arch / Inbound Adaptor
