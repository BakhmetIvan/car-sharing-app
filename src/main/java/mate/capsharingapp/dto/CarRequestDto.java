package mate.capsharingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CarRequestDto {
    @NotBlank(message = "The model field can't be blank")
    @Length(max = 255, message = "The model field can't be longer than 255")
    private String model;
    @NotBlank(message = "The brand field can't be blank")
    @Length(max = 255, message = "The brand field can't be longer than 255")
    private String brand;
    @NotBlank(message = "The car type field can't be blank")
    @Length(max = 255, message = "The car type field can't be longer than 100")
    private String carType;
    @NotNull(message = "The inventory field can't be null")
    @Positive(message = "The inventory field should be positive")
    private int inventory;
    @NotNull(message = "The daily fee field can't be null")
    @Positive(message = "The daily fee field should be positive")
    private BigDecimal dailyFee;
}
