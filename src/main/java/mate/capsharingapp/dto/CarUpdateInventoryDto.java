package mate.capsharingapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarUpdateInventoryDto {
    @NotNull(message = "Inventory can't be null")
    @Positive(message = "Inventory should be positive")
    private int inventory;
}
