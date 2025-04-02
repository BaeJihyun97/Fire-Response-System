package fireresponsesystem.infra;
import fireresponsesystem.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
@Transactional
public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReactionRepository reactionRepository;

    @RequestMapping(
        value = "posts/{postId}/comments",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public Post addComment(
        @PathVariable(value = "postId") Long postId,
        @RequestBody AddCommentCommand addCommentCommand,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.orElseThrow(()-> new Exception("No Entity Found"));
        Post post = optionalPost.get();
        post.addComment(addCommentCommand);
        postRepository.save(post);
        return post;
    }

    @RequestMapping(value = "posts/{postId}/react",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public Post react(@PathVariable("postId") Long postId,
                      @RequestBody ReactCommand reactCommand,
                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        post.react(reactCommand.getUserId());
        postRepository.save(post);
        return post;
    }

    @RequestMapping(
        value = "posts",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8"
    )
    public List<Map<String, Object>> getPostsWithUserReactions(
        @RequestParam(value = "userId", required = false) String userId,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(post -> {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("postId", post.getPostId());
            postMap.put("userId", post.getUserId());
            postMap.put("eventId", post.getEventId());
            postMap.put("eventType", post.getEventType());
            postMap.put("longitude", post.getLongitude());
            postMap.put("latitude", post.getLatitude());
            postMap.put("blurredVideoUri", post.getBlurredVideoUri());
            postMap.put("reactionCount", post.getReactionCount());
            postMap.put("createdAt", post.getCreatedAt());
            postMap.put("hasUserReacted", userId != null ? post.hasUserReacted(userId) : false);
            postMap.put("comments", post.getComments().stream()
                .map(comment -> {
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("commentId", comment.getCommentId());
                    commentMap.put("userId", comment.getUserId());
                    commentMap.put("comment", comment.getComment());
                    commentMap.put("createdAt", comment.getCreatedAt());
                    return commentMap;
                })
                .collect(Collectors.toList()));
            return postMap;
        }).collect(Collectors.toList());
    }

}
//>>> Clean Arch / Inbound Adaptor
