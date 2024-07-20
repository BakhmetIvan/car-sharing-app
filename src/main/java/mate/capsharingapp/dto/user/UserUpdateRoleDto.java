package mate.capsharingapp.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserUpdateRoleDto {
    private String role;
}
