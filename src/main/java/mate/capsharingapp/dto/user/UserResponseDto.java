package mate.capsharingapp.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
