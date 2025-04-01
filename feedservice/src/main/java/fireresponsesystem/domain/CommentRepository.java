package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "comments", path = "comments")
public interface CommentRepository extends JpaRepository<Comment, String> {}
//>>> PoEAA / Repository
