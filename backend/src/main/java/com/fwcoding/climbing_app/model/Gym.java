package com.fwcoding.climbing_app.model;

import java.util.List;

import com.fwcoding.climbing_app.enums.GradingSystem;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name="gym")
public class Gym {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    private boolean isSetupComplete; // NOTE: added, needed in GYM settings, false by default

    @Enumerated(EnumType.STRING)
    private GradingSystem gradingSystem; // e.g. Font / V / others

    @ToString.Exclude
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wall> walls;

    @ToString.Exclude
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GymGrade> gymGrades; // e.g. red -> v1

    //NOTE: Deleted as it is duplicated with gymGrades
    // @ElementCollection
    // @CollectionTable(name="gym_grade_names", joinColumns=@JoinColumn(name="gid"))
    // @Column(name="grade_name")
    // private List<String> grades;

    private String photoUrl;

}
