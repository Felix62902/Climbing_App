package com.fwcoding.climbing_app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.model.Climb;

@Repository
public interface ClimbRepository extends JpaRepository<Climb,Long> {

    List<Climb> findBySession_User_Id(Long userId);

    // Find the single highest scoring climb for a user, excluding a specific climb ID
    // We order by Score DESC, then Grade DESC to break ties
    // Used for recalculating highest grade
    @Query("SELECT c FROM Climb c WHERE c.session.user.id = :userId AND c.id != :excludedClimbId AND (c.status = 'SEND' OR c.status = 'FLASH') ORDER BY c.score DESC")
    List<Climb> findTopClimbsExcluding(@Param("userId") Long userId, @Param("excludedClimbId") Long excludedClimbId, Pageable pageable);
}
