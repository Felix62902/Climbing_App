package com.fwcoding.climbing_app.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String passwordHash;
    private String email;

    private String gradingSystem;
}
