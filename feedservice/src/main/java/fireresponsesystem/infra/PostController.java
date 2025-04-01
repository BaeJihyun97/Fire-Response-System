package fireresponsesystem.infra;
import fireresponsesystem.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

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

}
//>>> Clean Arch / Inbound Adaptor
