package com.fwcoding.climbing_app.dto;

import lombok.Data;

@Data
public class UserPreferenceRequest {
    
    private String theme;         // e.g., "LIGHT", "DARK"
    private String gradingSystem; // e.g., "V_SCALE", "FONTAINEBLEAU"

    // Default Constructor (JSON needs this)
    public UserPreferenceRequest() {}

}