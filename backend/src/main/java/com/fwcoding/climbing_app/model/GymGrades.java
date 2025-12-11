package com.fwcoding.climbing_app.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="gym_grades")
public class GymGrades {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String gradeName;
    // add below ? - check against GradeReference
    private String gradeSystem; // e.g., "V/French"
    private String description; // e.g., "Beginner", "Intermediate", "Advanced"
    private String colorHex; // e.g., "#FF5733"

    @ElementCollection
    @CollectionTable(name="gym_grade_labels", joinColumns=@JoinColumn(name="gym_grades_id"))
    @Column(name="label")

    private List<String> labels; // e.g., ["sloper", "crimp", "overhang"]
    private Integer difficultyScore; // e.g., V0 = 1, V1 = 2, ..., V16 = 17

    @ManyToOne
    @JoinColumn(name="gid", nullable = false)
    private Gym gym;
}
