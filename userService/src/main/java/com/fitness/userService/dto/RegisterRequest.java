package com.fitness.userService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message= "Email is invalid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min=6, message = "Password must have atleast 6 Character")
    private String password;

    private String firstName;
    private String lastName;
}
