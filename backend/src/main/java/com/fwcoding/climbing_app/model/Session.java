package com.fwcoding.climbing_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

// sid, uid, gid, date
@Entity
@Data // Lombok: Generates Getters, Setters, ToString, etc. automatically
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;

    @Column(nullable = false)
    private LocalDate sessionDate; 

    // Optional: Notes about the session
    private String note;
    
    @ManyToOne // Many Sessions belong to One User - Foreign Key
    @JoinColumn(name = "uid", nullable = false) // creates the "uid" column in the Sessions table
    private User user;

    // Many Sessions happen at One Gym
    @ManyToOne
    @JoinColumn(name = "gid", nullable = false)
    private Gym gym;

    // Optional
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Climb> climbs;
}