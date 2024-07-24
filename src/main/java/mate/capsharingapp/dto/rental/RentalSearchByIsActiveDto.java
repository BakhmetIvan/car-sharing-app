package mate.capsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalSearchByIsActiveDto {
    private Long userId;
    @NotNull(message = "IsActive can't be null")
    private boolean isActive;
}
