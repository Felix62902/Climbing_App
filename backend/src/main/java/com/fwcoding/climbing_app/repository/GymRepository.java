package com.fwcoding.climbing_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fwcoding.climbing_app.model.Gym;

public interface GymRepository extends JpaRepository<Gym,Long>{

}
