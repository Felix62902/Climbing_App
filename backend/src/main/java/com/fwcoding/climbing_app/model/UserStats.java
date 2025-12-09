package com.fwcoding.climbing_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;


@Entity
@Data
@Table(name="user_stats")
public class UserStats {

    // shared key with user's ID as PK for this table too
    @Id 
    @Column(name="uid")
    private Long id;

    @OneToOne
    @MapsId // connect id to the User's uid
    @JoinColumn(name="uid")
    @ToString.Exclude
    private User user;

    private int totalXp;

    private int totalSends;

    // private int totalSessions;
    // private double totalClimbingTime; // in hours
    private int totalProjects;

    private int totalFlashes;

    private String highestGrade;
    private double averageScore;

    private double averageSuccessRate; // percentage
    
    // Last time these stats were updated (Good for debugging)
    private java.time.LocalDateTime lastUpdated;

}   
