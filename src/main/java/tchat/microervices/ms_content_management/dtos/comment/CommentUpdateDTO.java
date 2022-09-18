package tchat.microervices.ms_content_management.dtos.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentUpdateDTO {

    @NotNull
    @NotEmpty
    private String id;

    @NotNull
    @NotEmpty
    private String content;

}
