package com.ahimsarijalu.auth_service.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserDTO user;
}
