package mate.capsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalSearchByIsActiveDto {
    private String userId;
    @NotNull(message = "IsActive can't be null")
    private String isActive;
}
