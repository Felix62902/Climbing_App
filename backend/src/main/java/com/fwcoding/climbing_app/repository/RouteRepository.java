package com.fwcoding.climbing_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fwcoding.climbing_app.enums.RouteStatus;
import com.fwcoding.climbing_app.model.Route;
import com.fwcoding.climbing_app.model.Session;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long>{
    
    List<Session> findByUser_Id(Long userId);
    List<Route> findByUser_IdAndStatus(Long userId, RouteStatus status);
}
