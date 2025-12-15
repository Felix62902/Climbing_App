package com.fwcoding.climbing_app.dto;

import lombok.Data;
import com.fwcoding.climbing_app.model.User;

@Data
public class UserProfileResponse {

    private UserInfo info;
    private UserStatsDto stats;
    private UserPreferencesDto preferences;

    public UserProfileResponse(User user) {
        
        // 1. Map Basic Info
        this.info = new UserInfo();
        this.info.setUsername(user.getUsername());
        this.info.setFirstName(user.getFirstName());
        this.info.setLastName(user.getLastName());
        this.info.setEmail(user.getEmail());
        
        // 2. Map Stats & Calculate Averages
        this.stats = new UserStatsDto();
        if (user.getStats() != null) {
            var s = user.getStats(); // shorter variable name for readability
            
            this.stats.setXp(s.getTotalXp());
            this.stats.setHighestGrade(s.getHighestGrade());
            this.stats.setTotalFlashes(s.getTotalFlashes());
            this.stats.setTotalSends(s.getTotalSends()); // Add this so we see the count
            
            // --- THE LOGIC: Calculate Average on the fly ---
            // Formula: Total XP / Total Sends
            // Safety: Check for 0 sends to avoid "Divide by Zero" error
            if (s.getTotalSends() > 0) {
                double avg = (double) s.getTotalXp() / s.getTotalSends();
                this.stats.setAverageScore(Math.round(avg * 10.0) / 10.0); // Round to 1 decimal
            } else {
                this.stats.setAverageScore(0.0);
            }
        }
        
        // 3. Map Prefs
        this.preferences = new UserPreferencesDto();
        if (user.getPreferences() != null) {
            this.preferences.setGradingSystem(user.getPreferences().getGradingSystem().name());
            this.preferences.setTheme(user.getPreferences().getTheme());
        }
    }

    // --- Inner Static Classes ---
    
    @Data
    public static class UserInfo {
        private String username;
        private String firstName;
        private String lastName;    
        private String email;
    }

    @Data
    public static class UserStatsDto {
        private int xp;
        private String highestGrade;
        private int totalFlashes;
        private int totalSends;     
        private double averageScore;
    }

    @Data
    public static class UserPreferencesDto {
        private String gradingSystem;
        private String theme;
    }
}