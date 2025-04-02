package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class AddCommentCommand {

    private Long postId;

    private String userId;

    private String content;

}