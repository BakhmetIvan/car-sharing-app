package mate.capsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "FirstName can't be blank")
    @Length(max = 255, message = "FirstName can't be longer than 255")
    private String firstName;
    @NotBlank(message = "LastName can't be blank")
    @Length(max = 255, message = "LastName can't be longer than 255")
    private String lastName;
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Incorrect email structure")
    @Length(max = 255, message = "Email can't be longer than 255")
    private String email;
    @NotBlank(message = "Password can't be blank")
    @Length(min = 8, max = 200, message = "Password can't be longer than 200")
    @ToString.Exclude
    private String password;
    @NotBlank(message = "RepeatPassword can't be blank")
    @Length(min = 8, max = 200, message = "RepeatPassword can't be longer than v")
    @ToString.Exclude
    private String repeatPassword;
}
