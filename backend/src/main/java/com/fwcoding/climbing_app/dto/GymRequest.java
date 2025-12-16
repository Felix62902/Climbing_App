package com.fwcoding.climbing_app.dto;

import java.util.List;


import lombok.Data;

@Data
public class GymRequest {
    private String name;
    private String location;
    // The Backend should calculate this  isSetupComplete (e.g. if walls > 0, set true).
    private String gradingSystem;
    private List<WallRequest> walls; // Optional
    private List<GymGradeRequest> gymGrades; // Optional
    private String photoUrl; // Optional


    @Data
    public class WallRequest {  
        private String name;      // "The Cave"
        private String photoUrl;
        private String defaultType; // "OVERHANG", "SLAB"
    }

    @Data
    public class GymGradeRequest {
        private String label;      // "V3"
        private String colorHex;   // "#FF0000"
        private Integer sortOrder; // 3
    }
}
