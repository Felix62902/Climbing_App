package com.fwcoding.climbing_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name="user_stats")

public class UserStats {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalClimbsAllTime;
    private int currentBoulderGrade;

    private double averageGrade;
    private int highestBoulderGrade;
    
    private int totalProjects;
    private int totalFlashAllTime;
    
    private double averageScore; // e.g., average difficulty score of climbs

    private int totalXP; // experience points

    private LocalDateTime lastUpdated;

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
