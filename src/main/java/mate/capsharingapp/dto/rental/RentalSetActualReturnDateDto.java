package mate.capsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalSetActualReturnDateDto {
    @NotNull(message = "Actual return date can't be null")
    private LocalDate actualReturnDate;
}
