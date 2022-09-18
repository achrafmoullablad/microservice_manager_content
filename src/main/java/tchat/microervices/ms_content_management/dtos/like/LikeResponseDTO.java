package tchat.microervices.ms_content_management.dtos.like;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeResponseDTO {

    private Long userId;

    private String type;
}
