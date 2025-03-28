package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class AiAnalysisHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<AiAnalysis>> {

    @Override
    public EntityModel<AiAnalysis> process(EntityModel<AiAnalysis> model) {
        return model;
    }
}
