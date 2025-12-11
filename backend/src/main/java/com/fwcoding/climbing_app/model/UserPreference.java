package com.fwcoding.climbing_app.model;

import com.fwcoding.climbing_app.enums.GradingSystem;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name="user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "uid", nullable = false, unique = true)
    private User user;

    // This links to your User table
    @Column(nullable = false, unique = true)
    private Long uid;  // same as user ID

    @Column(nullable = false)
    private String theme;  // "light" or "dark"

    @Column(nullable = false)
    private GradingSystem gradingSystem; // "V", "Font",
}