package tchat.microervices.ms_content_management.dtos.comment;

import lombok.*;
import tchat.microervices.ms_content_management.vos.User;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentResponseDTO {

    private String id;

    private String content;

    private Date createdAt;
    private Date updatedAt;

    private User user;

}
