package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class EventHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Event>> {

    @Override
    public EntityModel<Event> process(EntityModel<Event> model) {
        model.add(
            Link
                .of(
                    model.getRequiredLink("self").getHref() + "/updateeventtype"
                )
                .withRel("updateeventtype")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/updatestatus")
                .withRel("updatestatus")
        );

        return model;
    }
}
