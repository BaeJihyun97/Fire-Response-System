package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserAlarmHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<UserAlarm>> {

    @Override
    public EntityModel<UserAlarm> process(EntityModel<UserAlarm> model) {
        return model;
    }
}
