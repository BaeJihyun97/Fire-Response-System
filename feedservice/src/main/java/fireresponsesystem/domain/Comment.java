package fireresponsesystem.domain;

import fireresponsesystem.FeedserviceApplication;
import fireresponsesystem.domain.PostPublished;
import fireresponsesystem.domain.PostUpdated;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "Comment_table")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    @Column(name = "post_id")
    private Long postId;

    private Long userId;

    private String comment;

    private Date createdAt;

    private Date updatedAt;

    @PostPersist
    public void onPostPersist() {
        // CommentAdded commentAdded = new CommentAdded(this);
        // commentAdded.publishAfterCommit();

        // CommentUpdated commentUpdated = new CommentUpdated(this);
        // commentUpdated.publishAfterCommit();
    }

    public static CommentRepository repository() {
        CommentRepository commentRepository = FeedserviceApplication.applicationContext.getBean(
            CommentRepository.class
        );
        return commentRepository;
    }
}