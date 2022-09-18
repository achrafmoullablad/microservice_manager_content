package tchat.microervices.ms_content_management.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@NoArgsConstructor
public class Post {
    @Id
    private String id;
    private String content;
    private String photoOrVideo;
    private String type;
    private Long nbrShares = 0L;
    private Long nbrLikes = 0L;
    private Long nbrComments = 0L;

    private Date createdAt;

    private Date updatedAt;

    private Long userId;

    @DBRef
    private Post post;
    private boolean isTypeShare;
}
