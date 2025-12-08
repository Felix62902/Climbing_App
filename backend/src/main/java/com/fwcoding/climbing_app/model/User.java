package com.fwcoding.climbing_app.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="user")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
    // user has one set of stats
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserStats stats;
    // user has one set of preferences
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserPreferences preferences;
    
}
