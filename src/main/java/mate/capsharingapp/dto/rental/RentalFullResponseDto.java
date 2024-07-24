package mate.capsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import mate.capsharingapp.dto.car.CarFullResponseDto;
import mate.capsharingapp.dto.user.UserResponseDto;

@Data
public class RentalFullResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarFullResponseDto carFullResponseDto;
    private UserResponseDto userResponseDto;
}
