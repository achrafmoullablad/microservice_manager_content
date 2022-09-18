package tchat.microervices.ms_content_management.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Like {

    @Id
    private String id;

    private String type;

    @DBRef
    private Post post;

    private Long userId;

}
