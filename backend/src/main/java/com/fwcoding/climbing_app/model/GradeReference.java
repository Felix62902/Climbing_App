package com.fwcoding.climbing_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "gradeReference")
public class GradeReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String huecoScaleLabel;
    private String frenchScaleLabel;
    private Integer points;
    private Integer sortOrder;
}
