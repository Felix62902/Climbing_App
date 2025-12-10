package com.fwcoding.climbing_app.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClimbResponse {
    private Long id; // The Climb ID
    
    private String grade;
    private String color;
    private String status;
    private String note;
    
    private String wallName; 
    private String wallType; // "SLAB"
    
    // Maybe show the Project name if it exists
    private Long routeId; //NUllable
    
    private LocalDateTime createdDate;
}