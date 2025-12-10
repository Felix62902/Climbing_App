package com.fwcoding.climbing_app.service;

import org.springframework.stereotype.Service;

import com.fwcoding.climbing_app.dto.ClimbRequest;
import com.fwcoding.climbing_app.dto.ClimbResponse;
import com.fwcoding.climbing_app.enums.ClimbStatus;
import com.fwcoding.climbing_app.enums.WallType;
import com.fwcoding.climbing_app.model.Climb;
import com.fwcoding.climbing_app.model.Route;
import com.fwcoding.climbing_app.model.Session;
import com.fwcoding.climbing_app.model.UserStats;
import com.fwcoding.climbing_app.model.Wall;
import com.fwcoding.climbing_app.repository.*;
import jakarta.transaction.Transactional;

// Required services:
// find all climb that belonged to a user
// allow an user to log a new climb in a session
// update the statuses of a climb (e.g. attempted->Completed)
// detlete a climb?

// logClimb (Create):
    // Save the climb.
    // Side Effect: Calculate points -> Add to User's totalXp.
    // Side Effect: Update User's highestGrade if this is a new personal best.
// updateClimb (Edit):
// Scenario: User logged a "Send" but meant "Flash".
// Side Effect: Recalculate the score difference. (Flash is worth more). Update User's totalXp by adding/subtracting the difference.
// deleteClimb (Delete):
// Scenario: User accidentally logged a duplicate V5.
// Side Effect: Subtract the points from the User's totalXp.
// getClimbsBySession (Read):
// Fetch all climbs for a specific session ID (to show the daily log).

@Service
public class ClimbService {

    private RouteRepository routeRepo;
    private ClimbRepository climbRepo;
    private UserRepository userRepo;
    private SessionRepository sessionRepo;
    private WallRepository wallRepo;
    private GradeReferenceRepository gradeRefRepo;
    private UserStatsRepo userStatsRepo;

    public ClimbService(ClimbRepository climbRepo, UserRepository userRepo, SessionRepository sessionRepo, WallRepository wallRepo, GradeReferenceRepository gradeRefRepo, RouteRepository routeRepository, UserStatsRepo userStatsRepo){
        this.climbRepo = climbRepo;
        this.userRepo = userRepo;
        this.sessionRepo =sessionRepo;
        this.wallRepo = wallRepo;
        this.gradeRefRepo = gradeRefRepo;
        this.routeRepository = routeRepository;
        this.userStatsRepo = userStatsRepo;
    }

    @Transactional
    public ClimbRequest LogClimb(ClimbRequest request){
        // A. Save Climb
        // B. Calculate Score
        // C. Update UserStats (Add XP)
        // 1: Convert DTO to entity
        Session session = sessionRepo.findById(request.getSessionId())
            .orElseThrow(()-> new RuntimeException("Session not found"));
        Wall wall = wallRepo.findById(request.getWallId())
            .orElseThrow(()->new RuntimeException("Session not found"));

        Climb climb = new Climb();
        climb.setSession(session);
        climb.setWall(wall);
        if (request.getRouteId() != null) {
            Route route = routeRepo.findById(request.getRouteId()).orElse(null);
            climb.setRoute(route);
        } else {
        climb.setRoute(null); // Explicitly fine
        }
        climb.setGrade(request.getGrade());
        climb.setColor(request.getColor());

        try{
            climb.setStatus(ClimbStatus.valueOf(request.getStatus()));
        } catch(IllegalArgumentException e){
            throw new RuntimeException("Invalid status: " + request.getStatus());
        }

        if (request.getAttempts() == null || request.getAttempts() < 1) {
            climb.setAttempts(1);
        } else {
            climb.setAttempts(request.getAttempts());
        }

        if (request.getWallType() != null){
            try{
                WallType wallType = WallType.valueOf((request.getWallType().toUpperCase()));
                climb.setWallType(wallType);
            } catch(IllegalArgumentException e){
                climb.setWallType(wall.getDefaultType());
            }
        } else{
            climb.setWallType(wall.getDefaultType());
        }

        climb.setNote(request.getNote());
        // 2. points Calculation
        int points = 0;
        var gradeRef = gradeRefRepo.findByAnyLabel(climb.getGrade());

        if (gradeRef.isPresent()){
            points = gradeRef.get().getPoints();
            if (climb.getStatus()==ClimbStatus.FLASH){
                points *= 1.1;
            }
        }
        climb.setScore(points);
        // points are reduced for each attempts

        // 3. Update user stats
        updateUserStats(session.getUser(), points, climb.getStatus());

        Climb saved = climbRepo.save(climb);
        return mapToResponse(saved);
    }

    // THis is necessary as user may accidentally log an attempt (slippery finger/dup) and want to undo it
    @Transactional
    public void deleteClimb(Long climbId){
        Climb climb = climbRepo.findById(climbId)
                .orElseThrow(() -> new RuntimeException("Climb not found with id: " + climbId));

        //subtract the points and decrement the counters
        // Navigate to the user stats: Climb -> Session -> User -> Stats
        UserStats stats = climb.getSession().getUser().getStats();
        
        // Subtract the XP
        if (climb.getScore() != null) {
            stats.setTotalXp(stats.getTotalXp() - climb.getScore());
        }

        // Decrement totals flash/ Sent based on what the climb was
        if (climb.getStatus() == ClimbStatus.SEND || climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalSends(stats.getTotalSends() - 1);
        }
        
        if (climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalFlashes(stats.getTotalFlashes() - 1);
        }
        
        // Save the updated stats
        userStatsRepo.save(stats);

        // delete the climb record
        climbRepo.delete(climb);
    }

    public ClimbResponse getClimbById(Long id) {
        Climb climb = climbRepo.findById(id).orElseThrow();
        return mapToResponse(climb);
    }

    // Helper method to convert Entity -> Response DTO
    private ClimbResponse mapToResponse(Climb climb) {
        ClimbResponse response = new ClimbResponse();
        
        response.setId(climb.getId());
        response.setGrade(climb.getGrade());
        response.setStatus(climb.getStatus().name());
        
        response.setWallName(climb.getWall().getName()); 
        response.setWallType(climb.getWall().getDefaultType().name());
        
        if (climb.getRoute() != null) {
            response.setRouteId(climb.getRoute().getId());
        }
        
        return response;
    }

}
