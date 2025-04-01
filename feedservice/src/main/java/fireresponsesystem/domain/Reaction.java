package fireresponsesystem.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Reaction() {
        this.createdAt = LocalDateTime.now();
    }

    public static Reaction create(Post post, Long userId) {
        Reaction reaction = new Reaction();
        reaction.setPostId(post.getPostId());
        reaction.setUserId(userId);
        return reaction;
    }
}