package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class PostHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Post>> {

    @Override
    public EntityModel<Post> process(EntityModel<Post> model) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/addcomment")
                .withRel("addcomment")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "//react")
                .withRel("/react")
        );

        return model;
    }
}
