package fireresponsesystem.domain;

import fireresponsesystem.FeedserviceApplication;
import fireresponsesystem.domain.PostPublished;
import fireresponsesystem.domain.PostUpdated;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "Post_table")
@Data
@Component
//<<< DDD / Aggregate Root
public class Post {

    @Value("${post.reaction.threshold}")
    private Integer reactionThreshold;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postId;

    @Column(nullable = true)
    private String blurredVideoUri;

    private Long userId;

    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'NON_FIRE'")
    private EventType eventType = EventType.NON_FIRE;

    @Column(nullable = true)
    private Double longitude;

    @Column(nullable = true)
    private Double latitude;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private List<Reaction> reactions = new ArrayList<>();

    private Integer reactionCount = 0;

    @PostPersist
    public void onPostPersist() {
        // PostPublished postPublished = new PostPublished(this);
        // postPublished.publishAfterCommit();

        // PostUpdated postUpdated = new PostUpdated(this);
        // postUpdated.publishAfterCommit();
    }

    public static PostRepository repository() {
        PostRepository postRepository = FeedserviceApplication.applicationContext.getBean(
            PostRepository.class
        );
        return postRepository;
    }

    public static CommentRepository commentRepository() {
        CommentRepository commentRepository = FeedserviceApplication.applicationContext.getBean(
            CommentRepository.class
        );
        return commentRepository;
    }

    public static ReactionRepository reactionRepository() {
        ReactionRepository reactionRepository = FeedserviceApplication.applicationContext.getBean(
            ReactionRepository.class
        );
        return reactionRepository;
    }

    //<<< Clean Arch / Port Method
    public void addComment(AddCommentCommand addCommentCommand) {
        repository().findById(addCommentCommand.getPostId()).ifPresent(post->{
            Comment comment = new Comment();
            comment.setPostId(post.getPostId());
            comment.setUserId(addCommentCommand.getUserId());
            comment.setComment(addCommentCommand.getContent());
            commentRepository().save(comment);
        });

        // CommentAdded commentAdded = new CommentAdded(this);
        // commentAdded.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void react(Long userId) {
        if (!hasUserReacted(userId)) {
            Reaction reaction = Reaction.create(this, userId);
            reactionRepository().save(reaction);
            this.reactionCount++;
            repository().save(this);

            if (this.reactionCount == reactionThreshold) {
                Reacted reacted = new Reacted(this);
                reacted.publishAfterCommit();
            }
        }
    }

    public boolean hasUserReacted(Long userId) {
        return this.getReactions().stream()
            .anyMatch(reaction -> reaction.getUserId().equals(userId));
    }

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void publishPost(EventCreated eventCreated) {
        Post post = new Post();
        post.setEventId(eventCreated.getEventId());
        post.setUserId(eventCreated.getUserId());
        post.setLatitude(eventCreated.getLatitude());
        post.setLongitude(eventCreated.getLongitude());
        post.setEventType(EventType.valueOf(eventCreated.getEventType()));
        repository().save(post);

        // PostPublished postPublished = new PostPublished(post);
        // postPublished.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updatePost(FaceBlurred faceBlurred) {
        repository().findById(faceBlurred.getEventId()).ifPresent(post->{
            post.setBlurredVideoUri(faceBlurred.getBlurredVideoUri());
            repository().save(post);
         });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updatePost(
        IdentifiedAsFireEvent identifiedAsFireEvent
    ) {
       repository().findById(identifiedAsFireEvent.getEventId()).ifPresent(post->{
            post.setEventType(EventType.valueOf(identifiedAsFireEvent.getEventType()));
            repository().save(post);
         });
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
