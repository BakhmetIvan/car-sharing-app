package com.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
public class UserUpdateDto {
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
}
