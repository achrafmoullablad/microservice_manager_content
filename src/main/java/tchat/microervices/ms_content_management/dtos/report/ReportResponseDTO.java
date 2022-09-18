package tchat.microervices.ms_content_management.dtos.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.vos.User;

import java.util.Date;

@Data
@NoArgsConstructor
public class ReportResponseDTO {

    private String id;
    private String reason;

    private Post post;

    private Comment comment;

    private User user;

    private Date sendedAt;
}
