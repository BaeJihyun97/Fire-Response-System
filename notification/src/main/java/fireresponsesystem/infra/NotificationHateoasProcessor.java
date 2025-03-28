package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class NotificationHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Notification>> {

    @Override
    public EntityModel<Notification> process(EntityModel<Notification> model) {
        return model;
    }
}
