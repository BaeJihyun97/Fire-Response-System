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
Public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String userId;

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

    // //<<< Clean Arch / Port Method
    // public void addComment() {
    //     //implement business logic here:

    //     CommentAdded commentAdded = new CommentAdded(this);
    //     commentAdded.publishAfterCommit();
    // }
}