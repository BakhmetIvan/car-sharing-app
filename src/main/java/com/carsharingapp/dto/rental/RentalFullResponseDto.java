package com.carsharingapp.dto.rental;

import com.carsharingapp.dto.car.CarFullResponseDto;
import com.carsharingapp.dto.user.UserResponseDto;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalFullResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarFullResponseDto car;
    private UserResponseDto user;
}
