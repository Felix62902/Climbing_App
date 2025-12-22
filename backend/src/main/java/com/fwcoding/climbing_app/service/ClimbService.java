package com.fwcoding.climbing_app.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
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
        int points = calculatePoints(climb);
        climb.setScore(points);

        // 3. Update User Stats (and auto-save via cascade/transaction)
        updateUserStats(session.getUser(), points, climb);

        Climb saved = climbRepo.save(climb);
        return mapToResponse(saved);
    }

    @Transactional
    public ClimbResponse updateClimb(Long climbId, ClimbRequest request){
        //adjust point if changes flash-> send, updated attempts, grade change etc
        // logic: subtract old points from user, recalculate new point, update climb details, add new points
        Climb climb = climbRepo.findById(climbId)
            .orElseThrow(() -> new RuntimeException("Climb not found with id: " + climbId));

        User user = climb.getSession().getUser();

        //  1: Revert the OLD stats 
        // treat the old version of the climb as if it's being deleted from the stats
        revertUserStats(user, climb);

        // STEP 2: Update the Climb Fields with NEW data
        if (request.getGrade() != null) climb.setGrade(request.getGrade());
        if (request.getNote() != null) climb.setNote(request.getNote());
        if (request.getColor() != null) climb.setColor(request.getColor());
        if (request.getAttempts() != null) climb.setAttempts(request.getAttempts());
        
        if (request.getStatus() != null) {
            try {
                climb.setStatus(ClimbStatus.valueOf(request.getStatus()));
            } catch(IllegalArgumentException e) {
                throw new RuntimeException("Invalid status");
            }
        }
        // STEP 3: Recalculate Points for the NEW data
        int newPoints = calculatePoints(climb);
        climb.setScore(newPoints);  

        // STEP 4: Apply the NEW stats (Redo the future)
        updateUserStats(user, newPoints, climb);

        // STEP 5: Save
        userRepo.save(user); 
        Climb saved = climbRepo.save(climb);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteClimb(Long climbId) {
        Climb climb = climbRepo.findById(climbId)
                .orElseThrow(() -> new RuntimeException("Climb not found with id: " + climbId));

        // Use the helper!
        revertUserStats(climb.getSession().getUser(), climb);

        // Save the User Stats updates
        userRepo.save(climb.getSession().getUser());

        climbRepo.delete(climb);
    }

    public ClimbResponse getClimbById(Long climbId){
        Climb climb = climbRepo.findById(climbId)
                .orElseThrow(() -> new RuntimeException("Climb not found with id: " + climbId));
        return mapToResponse(climb);        
    }

    // -----------------------------helper methods ----------------------------------------------

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

    // Helper: Reverses the stats gained from a specific climb
    private void revertUserStats(User user, Climb climb) {
        UserStats stats = user.getStats();

        // 1. Revert XP
        if (climb.getScore() != null) {
            int newTotal = Math.max(0, stats.getTotalXp() - climb.getScore());
            stats.setTotalXp(newTotal);
        }

        // 2. Revert Counters (Sends/Flashes)
        if (climb.getStatus() == ClimbStatus.SEND || climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalSends(Math.max(0, stats.getTotalSends() - 1));
            
            //3.  Handle Downgrading Personal Best
            // Check if the climb we are removing/changing IS the current Personal Best compare the points
            if (climb.getScore() != null && climb.getScore().equals(stats.getHighestGradePoints())) {
                
                // Query DB for the "Next Best" climb
                List<Climb> nextBestList = climbRepo.findTopClimbsExcluding(
                    user.getId(), 
                    climb.getId(), 
                    PageRequest.of(0, 1)
                );

                if (!nextBestList.isEmpty()) {
                    Climb nextBest = nextBestList.get(0);
                    // Found a runner-up, Set stats to this climb.
                    stats.setHighestGrade(nextBest.getGrade());
                    stats.setHighestGradePoints(nextBest.getScore());
                } else {
                    // No other climbs found (User deleted their only send)
                    stats.setHighestGrade("V0"); // Or null
                    stats.setHighestGradePoints(0);
                }
            }
        }

        // Revert Flash Counter
        if (climb.getStatus() == ClimbStatus.FLASH) {
            stats.setTotalFlashes(Math.max(0, stats.getTotalFlashes() - 1));
        }
    }

    // Helper: Centralized logic for calculating points
    private int calculatePoints(Climb climb) {
        int points = 0;

        // 1. Fetch Base Points for Grade
        var gradeRef = gradeRefRepo.findByAnyLabel(climb.getGrade());
        if (gradeRef.isPresent()) {
            points = gradeRef.get().getPoints();
            
            // 2. Apply Flash Bonus (10%)
            if (climb.getStatus() == ClimbStatus.FLASH) {
                points = (int) (points * 1.1);
            }
        }

        // 3. Apply Attempt Penalty
        int safeAttempts = (climb.getAttempts() == null || climb.getAttempts() < 1) ? 1 : climb.getAttempts();
        int penalty = (safeAttempts - 1) * 10;

        // 4. Ensure score doesn't drop below zero
        return Math.max(0, points - penalty);
    }
}