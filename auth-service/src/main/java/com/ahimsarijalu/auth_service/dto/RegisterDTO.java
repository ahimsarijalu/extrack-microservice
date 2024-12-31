package com.ahimsarijalu.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "Email should not be empty or null")
    @Email(message = "Email should be a valid email address")
    private String email;

    @NotBlank(message = "Name should not be empty or null")
    private String name;

    @NotBlank(message = "Password should not be empty or null")
    private String password;

    @NotNull(message = "Age should not be null")
    @Min(value = 13, message = "Age should be at least 13")
    private Integer age;
}
