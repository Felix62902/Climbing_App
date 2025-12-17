package com.fwcoding.climbing_app.model;

import com.fwcoding.climbing_app.enums.GradingSystem;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;


@Entity
@Data
@Table(name="user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId // connect with user id to make sure both are identical
    @JoinColumn(name = "user_id") 
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String theme;  // "light" or "dark"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradingSystem gradingSystem; // "V", "Font",
}