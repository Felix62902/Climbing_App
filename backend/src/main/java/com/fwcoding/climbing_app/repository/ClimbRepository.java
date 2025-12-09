package com.fwcoding.climbing_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.model.Climb;

@Repository
public interface ClimbRepository extends JpaRepository<Climb,Long> {

    List<Climb> findBySession_User_Id(Long userId);
}
