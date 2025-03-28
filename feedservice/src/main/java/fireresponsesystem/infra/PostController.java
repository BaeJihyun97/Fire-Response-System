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
// @RequestMapping(value="/posts")
@Transactional
public class PostController {
    @Autowired
    PostRepository postRepository;



    @RequestMapping(value = "posts/{id}/addcomment",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8")
    public Post addComment(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("##### /post/addComment  called #####");
            Optional<Post> optionalPost = postRepository.findById(id);
            
            optionalPost.orElseThrow(()-> new Exception("No Entity Found"));
            Post post = optionalPost.get();
            post.addComment();
            
            postRepository.save(post);
            return post;
            
    }
    




    @RequestMapping(value = "posts//react",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public Post react(HttpServletRequest request, HttpServletResponse response, 
        ) throws Exception {
            System.out.println("##### /post/react  called #####");
            post.react();
            postRepository.save(post);
            return post;
    }

}
//>>> Clean Arch / Inbound Adaptor
