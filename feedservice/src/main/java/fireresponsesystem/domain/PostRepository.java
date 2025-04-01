package fireresponsesystem.domain;

import fireresponsesystem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "posts", path = "posts")
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPostId(Long postId);
    Optional<Post> findByEventId(Long eventId);
}
//>>> PoEAA / Repository
