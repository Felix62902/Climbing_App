package com.fwcoding.climbing_app.dto;

import java.util.List;
import com.fwcoding.climbing_app.enums.GradingSystem;
import lombok.Data;

@Data
public class GymResponse {
    private Long id;
    private String name;
    private String location;
    private boolean isSetupComplete; // NOTE: added, needed in GYM settings, false by default
    private GradingSystem gradingSystem; // e.g. Font / V / others
    private List<WallResponse> walls;
    private List<GymGradeResponse> gymGrades; // e.g. red -> v1
    private String photoUrl;

    @Data
    public static class WallResponse{
        private Long id;
        private String user;
        private String name;
        private String photoUrl;
        private String defaultType;
    }

    @Data
    public static class GymGradeResponse{
        private Long id;
        private String label;      // "V3"
        private String colorHex;   // "#FF0000"
        private Integer sortOrder; // 3
    }
}
