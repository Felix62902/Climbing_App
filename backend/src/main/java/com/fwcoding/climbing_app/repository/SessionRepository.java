package com.fwcoding.climbing_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.model.Session;

// ability: save(), findById(),findAll(),delete(),count() + customer methods
@Repository
public interface SessionRepository extends JpaRepository<Session,Long>{

    //Find Sessions where the Session's User's ID is X.
    List<Session> findByUser_Id(Long userId);

    // Find the most recent session where EndTime is EMPTY (Active)
    Optional<Session> findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(Long userId);
}
