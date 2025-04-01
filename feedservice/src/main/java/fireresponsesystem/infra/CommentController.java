package fireresponsesystem.infra;
import fireresponsesystem.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;


@RestController
@Transactional
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    // @RequestMapping(
    //     value = "posts/{postId}/comments",
    //     method = RequestMethod.POST,
    //     produces = "application/json;charset=UTF-8"
    // )
    // public Comment createComment(
    //     @PathVariable(value = "postId") Long postId,
    //     @RequestBody AddCommentCommand addCommentCommand,
    //     HttpServletRequest request,
    //     HttpServletResponse response
    // ) throws Exception {
    //     Optional<Post> optionalPost = postRepository.findById(postId);
    //     optionalPost.orElseThrow(()-> new Exception("No Entity Found"));
    //     Post post = optionalPost.get();
    //     Comment comment = new Comment();
    //     comment.setPost(post);
    //     comment.setUserId(addCommentCommand.getUserId());
    //     comment.setComment(addCommentCommand.getContent());
    //     post.getComments().add(comment);
    //     postRepository.save(post);
    //     return comment;
    // }

}