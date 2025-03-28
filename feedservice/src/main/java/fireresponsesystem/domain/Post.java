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
@Table(name = "Post_table")
@Data
//<<< DDD / Aggregate Root
public class Post {

    @Id
    private String postId;

    private String blurredVideoUri;

    private String userId;

    private String eventId;

    @ElementCollection
    private List<String> comments;

    private Integer reactionCount;

    private Date createdAt;

    private Date updatedAt;

    @PostPersist
    public void onPostPersist() {
        PostPublished postPublished = new PostPublished(this);
        postPublished.publishAfterCommit();

        PostUpdated postUpdated = new PostUpdated(this);
        postUpdated.publishAfterCommit();
    }

    public static PostRepository repository() {
        PostRepository postRepository = FeedserviceApplication.applicationContext.getBean(
            PostRepository.class
        );
        return postRepository;
    }

    //<<< Clean Arch / Port Method
    public void addComment() {
        //implement business logic here:

        CommentAdded commentAdded = new CommentAdded(this);
        commentAdded.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void react() {
        //implement business logic here:

        Reacted reacted = new Reacted(this);
        reacted.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void publishPost(EventCreated eventCreated) {
        //implement business logic here:

        /** Example 1:  new item 
        Post post = new Post();
        repository().save(post);

        PostPublished postPublished = new PostPublished(post);
        postPublished.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(eventCreated.get???()).ifPresent(post->{
            
            post // do something
            repository().save(post);

            PostPublished postPublished = new PostPublished(post);
            postPublished.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void postUpdated(FaceBlurred faceBlurred) {
        //implement business logic here:

        /** Example 1:  new item 
        Post post = new Post();
        repository().save(post);

        PostUpdated postUpdated = new PostUpdated(post);
        postUpdated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(faceBlurred.get???()).ifPresent(post->{
            
            post // do something
            repository().save(post);

            PostUpdated postUpdated = new PostUpdated(post);
            postUpdated.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void postUpdated(
        IdentifiedAsFireEvent identifiedAsFireEvent
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Post post = new Post();
        repository().save(post);

        PostUpdated postUpdated = new PostUpdated(post);
        postUpdated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(identifiedAsFireEvent.get???()).ifPresent(post->{
            
            post // do something
            repository().save(post);

            PostUpdated postUpdated = new PostUpdated(post);
            postUpdated.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
