package com.example.UserService.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String id;
    private String name;
    private String email;
    private String role;
}
