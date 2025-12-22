package com.fwcoding.climbing_app.dto;

import lombok.Data;

@Data
public class UserPreferenceResponse {
    
    private String theme;
    private String gradingSystem;

    // Default Constructor
    public UserPreferenceResponse() {}

    //  The Constructor your Service uses
    public UserPreferenceResponse(String theme, String gradingSystem) {
        this.theme = theme;
        this.gradingSystem = gradingSystem;
    }
}