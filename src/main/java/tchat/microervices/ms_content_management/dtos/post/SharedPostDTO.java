package tchat.microervices.ms_content_management.dtos.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class SharedPostDTO {

    @NotNull
    @NotEmpty
    private String postId;

    @NotNull
    private Long userId;

    private String content;
}
