package com.fwcoding.climbing_app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Use Spring's Transactional

import com.fwcoding.climbing_app.dto.ClimbRequest;
import com.fwcoding.climbing_app.dto.ClimbResponse;
import com.fwcoding.climbing_app.enums.ClimbStatus;
import com.fwcoding.climbing_app.enums.WallType;
import com.fwcoding.climbing_app.model.Climb;
import com.fwcoding.climbing_app.model.Route;
import com.fwcoding.climbing_app.model.Session;
import com.fwcoding.climbing_app.model.User;
import com.fwcoding.climbing_app.model.UserStats;
import com.fwcoding.climbing_app.model.Wall;
import com.fwcoding.climbing_app.repository.*;

@Service
public class ClimbService {

    private final ClimbRepository climbRepo;
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final WallRepository wallRepo;
    private final GradeReferenceRepository gradeRefRepo;
    private final RouteRepository routeRepo; // Fixed naming

    public ClimbService(ClimbRepository climbRepo, 
                        UserRepository userRepo, 
                        SessionRepository sessionRepo, 
                        WallRepository wallRepo, 
                        GradeReferenceRepository gradeRefRepo, 
                        RouteRepository routeRepo) { // Match param name
        this.climbRepo = climbRepo;
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.wallRepo = wallRepo;
        this.gradeRefRepo = gradeRefRepo;
        this.routeRepo = routeRepo; // FIX: Matches field name
    }

    @Transactional
    public ClimbResponse logClimb(ClimbRequest request) { // Lowercase 'l'
        Session session = sessionRepo.findById(request.getSessionId())
            .orElseThrow(() -> new RuntimeException("Session not found"));
            
        Wall wall = wallRepo.findById(request.getWallId())
            .orElseThrow(() -> new RuntimeException("Wall not found")); // FIX: Correct message

        // 1. Climb Object Setup
        Climb climb = new Climb();
        climb.setSession(session);
        climb.setWall(wall);
        
        if (request.getRouteId() != null) {
            Route route = routeRepo.findById(request.getRouteId()).orElse(null);
            climb.setRoute(route);
        } else {
            climb.setRoute(null);
        }
        
        climb.setGrade(request.getGrade());
        climb.setColor(request.getColor());
        climb.setNote(request.getNote());

        try {
            climb.setStatus(ClimbStatus.valueOf(request.getStatus()));
        } catch(IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + request.getStatus());
        }

        // Attempts Logic
        if (request.getAttempts() == null || request.getAttempts() < 1) {
            climb.setAttempts(1);
        } else {
            climb.setAttempts(request.getAttempts());
        }

        // WallType Logic
        if (request.getWallType() != null) {
            try {
                climb.setWallType(WallType.valueOf((request.getWallType().toUpperCase())));
            } catch(IllegalArgumentException e) {
                climb.setWallType(wall.getDefaultType());
            }
        } else {
            climb.setWallType(wall.getDefaultType());
        }

        // 2. Points Calculation
        int points = 0;
        var gradeRef = gradeRefRepo.findByAnyLabel(climb.getGrade());

        if (gradeRef.isPresent()) {
            points = gradeRef.get().getPoints();
            if (climb.getStatus() == ClimbStatus.FLASH) {
                points = (int) (points * 1.1); // Explicit cast is safer
            }
        }
        
        // Optional Penalty Logic
        int penalty = (climb.getAttempts() - 1) * 10;
        points = Math.max(0, points - penalty);

        climb.setScore(points);

        // 3. Update User Stats (and auto-save via cascade/transaction)
        updateUserStats(session.getUser(), points, climb);

        Climb saved = climbRepo.save(climb);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteClimb(Long climbId) {
        Climb climb = climbRepo.findById(climbId)
                .orElseThrow(() -> new RuntimeException("Climb not found with id: " + climbId));

        UserStats stats = climb.getSession().getUser().getStats();
        
        // 1. Revert Points
        if (climb.getScore() != null) {
            // Ensure we don't go below zero (safety check)
            int newTotal = Math.max(0, stats.getTotalXp() - climb.getScore());
            stats.setTotalXp(newTotal);
        }

        // 2. Revert Counters
        if (climb.getStatus() == ClimbStatus.SEND || climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalSends(Math.max(0, stats.getTotalSends() - 1));
        }
        
        if (climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalFlashes(Math.max(0, stats.getTotalFlashes() - 1));
        }
        
        // FIX: Save the USER, not the stats. 
        // UserRepository expects a User entity.
        userRepo.save(stats.getUser()); 

        climbRepo.delete(climb);
    }

    // Helper method to convert Entity -> Response DTO
    private ClimbResponse mapToResponse(Climb climb) {
        ClimbResponse response = new ClimbResponse();
        
        response.setId(climb.getId()); // Ensure your Climb model has getId()
        response.setGrade(climb.getGrade());
        response.setStatus(climb.getStatus().name());
        
        if (climb.getWall() != null) {
            response.setWallName(climb.getWall().getName()); 
            if (climb.getWall().getDefaultType() != null) {
                response.setWallType(climb.getWall().getDefaultType().name());
            }
        }
        
        if (climb.getRoute() != null) {
            response.setRouteId(climb.getRoute().getId()); // Ensure Route model has getId() (or getRid())
        }
        
        return response;
    }

    //Helper method to update user stats, total sends, total points, new PB etc
    private void updateUserStats(User user, int points, Climb climb) {
        UserStats stats = user.getStats();
        
        // Update XP
        stats.setTotalXp(stats.getTotalXp() + points);

        // Update Counters & Personal Bests
        if (climb.getStatus() == ClimbStatus.SEND || climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalSends(stats.getTotalSends() + 1);
            
            // Check for High Score
            if (points > stats.getHighestGradePoints()) {
                stats.setHighestGrade(climb.getGrade());       
                stats.setHighestGradePoints(points);       
            }
        }

        if (climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalFlashes(stats.getTotalFlashes() + 1);
        }
        
        // Note: No need to explicitly save here if called from within a @Transactional method
        // Hibernate will auto-update the managed User entity at the end of the transaction.
    }
}