package fireresponsesystem.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class AddCommentCommand {

    private Long postId;

    private Long userId;

    private String content;

}