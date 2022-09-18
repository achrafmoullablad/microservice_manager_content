package tchat.microervices.ms_content_management.dtos.like;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class LikeDTO {

    @NotNull
    @NotEmpty
    private String postId;

    @NotNull
    private Long userId;

    private String type;
}
