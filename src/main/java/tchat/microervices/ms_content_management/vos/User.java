package tchat.microervices.ms_content_management.vos;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User{

    private Long id;

    private String fullname;

    private String photo;

    private String profession;

    private boolean isLocked;

    private boolean isExpired;

}
