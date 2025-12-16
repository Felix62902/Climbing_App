package com.fwcoding.climbing_app.dto;

import lombok.Data;

@Data
public class SessionRequest {
    private Long gymId; 
    private String gymName;   // Used if creating a NEW gym
    // private String gymLocation; // Optional
}