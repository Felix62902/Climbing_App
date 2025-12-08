package com.fwcoding.climbing_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.model.Wall;

@Repository
public interface WallRepository extends JpaRepository<Wall, Long> {

    List<Wall> findByGym_IdAndUser_Id(Long gymId, Long userId);
}
