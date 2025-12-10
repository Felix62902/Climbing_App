package com.fwcoding.climbing_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.model.GradeReference;

@Repository
public interface GradeReferenceRepository extends JpaRepository<GradeReference, Long>{

    List<GradeReference> findByHuecoScaleLabel(String label);
    
    List<GradeReference> findByFrenchScaleLabel(String label);

    // "Find the row where the label matches Hueco OR matches French"
    @Query("SELECT g FROM GradeReference g WHERE g.huecoScaleLabel = :label OR g.frenchScaleLabel = :label")
    Optional<GradeReference> findByAnyLabel(@Param("label") String label);
}
