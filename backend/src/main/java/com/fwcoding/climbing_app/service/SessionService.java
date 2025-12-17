package com.fwcoding.climbing_app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fwcoding.climbing_app.dto.SessionRequest;
import com.fwcoding.climbing_app.dto.SessionResponse;
import com.fwcoding.climbing_app.enums.GradingSystem;
import com.fwcoding.climbing_app.model.Gym;
import com.fwcoding.climbing_app.model.Session;
import com.fwcoding.climbing_app.model.User;
import com.fwcoding.climbing_app.repository.GymRepository;
import com.fwcoding.climbing_app.repository.SessionRepository;
import com.fwcoding.climbing_app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SessionService {

    private final SessionRepository sessionRepo;
    private final UserRepository userRepo; 
    private final GymRepository gymRepo;   

    public SessionService(SessionRepository sessionRepo, UserRepository userRepo, GymRepository gymRepo){
        this.sessionRepo = sessionRepo;
        this.userRepo = userRepo;
        this.gymRepo = gymRepo;
    }

    @Transactional
    public SessionResponse startSession(Long userId, SessionRequest request){
        //rememeber to set Gym setup incomplete if gym not in list
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Gym gym;

        // 2. Determine Gym Strategy
        if (request.getGymId() != null) {
            // Scenario A: User selected an existing gym
            gym = gymRepo.findById(request.getGymId())
                .orElseThrow(() -> new RuntimeException("Gym not found"));
        } 
        else if (request.getGymName() != null && !request.getGymName().isEmpty()) {
            // Scenario B: User entered a NEW gym name
            //  create a "Draft" gym immediately
            gym = new Gym();
            gym.setName(request.getGymName());
            gym.setSetupComplete(false); // THE KEY FLAG
            
            // Set safe defaults so app doesn't crash
            gym.setGradingSystem(GradingSystem.V_SCALE); 
            gym.setLocation("Unknown"); 
            
            gym = gymRepo.save(gym);
        } 
        else {
            throw new RuntimeException("Request must contain either gymId or gymName");
        }

        Session session = new Session();
        session.setUser(user);
        session.setGym(gym);
        // session.setSessionDate(LocalDate.now());
        session.setStartTime(LocalDateTime.now());

        Session saved = sessionRepo.save(session);
        return mapToResponse(saved);
    }

    @Transactional
    public SessionResponse finishSession(Long sessionId, String note) {
        Session session = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setEndTime(LocalDateTime.now());
        session.setNote(note);

        Session saved = sessionRepo.save(session);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        // Because of CascadeType.ALL on Session->Climbs,
        // this deletes the session AND all climbs inside it.
        sessionRepo.deleteById(sessionId);
    }

    // --- Helper ---
    private SessionResponse mapToResponse(Session session) {
        SessionResponse response = new SessionResponse();
        response.setSessionId(session.getId());
        response.setGymName(session.getGym().getName());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setNote(session.getNote());

        // Useful: Tell frontend if gym setup is needed
        response.setGymSetupComplete(session.getGym().isSetupComplete());
        return response;
    }

    // future: Resume Session (if user phone died etc)
    // public SessionResponse getActiveSession(Long userId) {
    // // Find the most recent session with NO end time
    // // You'll need to add this method to your SessionRepository interface first!
    //     // return sessionRepo.findFirstByUser_IdAndEndTimeIsNullOrderByStartTimeDesc(userId)
    //     //    .map(this::mapToResponse)
    //     //    .orElse(null);
    // }
}
