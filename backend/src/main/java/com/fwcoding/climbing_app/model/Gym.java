package com.fwcoding.climbing_app.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="gym")
public class Gym {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    private String name;

    private String location;

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wall> walls;

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GymGrades> gymGrades;


    @ElementCollection
    @CollectionTable(name="gym_grade_names", joinColumns=@JoinColumn(name="gid"))
    @Column(name="grade_name")
    private List<String> grades;

    private String photoUrl;

}
