package mate.capsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.capsharingapp.dto.car.CarFullResponseDto;
import mate.capsharingapp.dto.user.UserResponseDto;

@Data
@Accessors(chain = true)
public class RentalFullResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarFullResponseDto carFullResponseDto;
    private UserResponseDto userResponseDto;
}
