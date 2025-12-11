package com.fwcoding.climbing_app.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

// Example: (1,V1,RED,2,3) the grade has id 1, labelled as V1 and Colored red within Gym with ID=3
@Entity
@Data
@Table(name="gym_grades")
public class GymGrade {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private String label; // e.g. "V3", "5.10a", or "Intermediate")

    @Column(nullable = false)
    private String colorHex; // e.g., "#FF5733"

    @Column(nullable = false)
    private Integer sortOrder;  //logic to help with sorting
 
    // private String gradeName;
    // add below ? - check against GradeReference
    // private String gradeSystem; // e.g., "V/Font"  NOTE: can be obtained from the parent (GYM) so not needed here
    // private String description; // e.g., "Beginner", "Intermediate", "Advanced" NOTE: may not be needed as gym dont usually name the labels
    

    // removed: label refer to the grades not the type of holds(which belongs to the route)
    // @ElementCollection
    // @CollectionTable(name="gym_grade_labels", joinColumns=@JoinColumn(name="gym_grades_id"))
    // @Column(name="label")

    @ManyToOne
    @JoinColumn(name="gid", nullable = false)
    @ToString.Exclude // Prevent infinite loop
    private Gym gym;
}
