package com.fwcoding.climbing_app.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name="user_stats")

public class UserStats {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalClimbs;

    
    private int totalSessions;

        private double averageGrade;
    
        private double totalClimbingTime; // in hours
    
        private int highestGrade;
    
        private int totalProjects;
    
        private int totalOnsights;
    
        private int totalFlashs;
    
        private int totalFalls;
    
        private double averageSuccessRate; // percentage



    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
