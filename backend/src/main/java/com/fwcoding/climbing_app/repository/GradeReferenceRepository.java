package com.fwcoding.climbing_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.model.GradeReference;

@Repository
public interface GradeReferenceRepository extends JpaRepository<GradeReference, Long>{

    List<GradeReference> findByHuecoScaleLabel(String label);
    
    List<GradeReference> findByFrenchScaleLabel(String label);
}
