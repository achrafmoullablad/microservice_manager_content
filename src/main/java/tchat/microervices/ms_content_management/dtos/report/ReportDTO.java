package tchat.microervices.ms_content_management.dtos.report;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ReportDTO {

    @NotNull
    @NotEmpty
    private String reason;


    private String postId;


    private String commentId;

    private Long userId;
}
