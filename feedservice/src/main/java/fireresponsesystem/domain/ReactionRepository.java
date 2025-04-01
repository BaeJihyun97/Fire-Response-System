package fireresponsesystem.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "reactions", path = "reactions")
public interface ReactionRepository extends JpaRepository<Reaction, Long> {}
//>>> PoEAA / Repository