package com.ahimsarijalu.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Email should not be empty or null")
    private String email;

    @NotBlank(message = "Password should not be empty or null")
    private String password;
}