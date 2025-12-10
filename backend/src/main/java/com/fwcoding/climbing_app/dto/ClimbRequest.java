package com.fwcoding.climbing_app.dto;
import lombok.Data;


// For POST and PUT climb requests   
@Data
public class ClimbRequest {
    // No ID here (The ID is in the URL for updates)
    
    private Long sessionId;
    private Long wallId;
    private Long projectId;
    private Long routeId;

    private String grade;
    private String color;
    private String status; // "FLASH", "SEND"
    private String note;
    private Integer attempts;
    private String wallType; // Optional, Overriding Wall if provided
}