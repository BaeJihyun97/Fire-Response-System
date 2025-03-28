package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class VideoAnalysisHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<VideoAnalysis>> {

    @Override
    public EntityModel<VideoAnalysis> process(
        EntityModel<VideoAnalysis> model
    ) {
        return model;
    }
}
