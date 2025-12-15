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

    // private int currentLevel;  //Beginner Advanced etc 


    private int totalFlashes;

    private String highestGrade;       // Visual: "V5" or "6c"
    private int highestGradePoints;     // Logic: 500 (The points value of that grade, cached value obtained from gradereference. needed for comparison as difficult to compare by string val e.g. V1->v5)

    // Last time these stats were updated (Good for debugging)
    private java.time.LocalDateTime lastUpdated;

}   
