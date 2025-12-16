package com.fwcoding.climbing_app.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SessionResponse {
    private Long sessionId;
    private String gymName;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // Null if active
    private String status;         // "IN_PROGRESS", "FINISHED"
    private String note;
    private boolean gymSetupComplete;
}