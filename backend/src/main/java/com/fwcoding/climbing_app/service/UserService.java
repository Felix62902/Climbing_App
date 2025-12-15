package com.fwcoding.climbing_app.service;

import org.springframework.stereotype.Service;

import com.fwcoding.climbing_app.dto.UserProfileResponse;
import com.fwcoding.climbing_app.dto.UserRegistrationRequest;
import com.fwcoding.climbing_app.enums.GradingSystem;
import com.fwcoding.climbing_app.model.User;
import com.fwcoding.climbing_app.model.UserPreference;
import com.fwcoding.climbing_app.model.UserStats;
import com.fwcoding.climbing_app.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User RegisterUser(UserRegistrationRequest request){
        User user = new User();
        UserPreference preference = new UserPreference();
        UserStats stats = new UserStats();

        // UserStats Setup
        stats.setUser(user);
        stats.setTotalXp(0);
        stats.setHighestGrade(null);


        //User Preference setup
        preference.setUser(user);
        preference.setTheme("LIGHT");
        preference.setGradingSystem(parseGradingSystem(request.getGradingSystem()));

        // User setup
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // TODO: Remember to hash this password later!
        user.setPasswordHash(request.getPasswordHash());
        user.setStats(stats);
        user.setPreferences(preference);

        return userRepository.save(user);
    }

    // gets the entire User Information, User stats, User preference
    public UserProfileResponse getProfile(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        return new UserProfileResponse(user);
    }


    @Transactional
    public User updateProfile(Long userId, UserRegistrationRequest request){
        User user = userRepository.findById(userId)
            .orElseThrow(()->new RuntimeException("User not found"));
        
        UserPreference preference = user.getPreferences();

        // --- User Update Logic ---
        // Only update fields if they are sent (Optional check)
        if(request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if(request.getLastName() != null) user.setLastName(request.getLastName());
        if(request.getEmail() != null) user.setEmail(request.getEmail());
        
        // --- Preference Update Logic ---
        // Only update Grading System if the user actually requested a change
        if (request.getGradingSystem() != null) {
            preference.setGradingSystem(parseGradingSystem(request.getGradingSystem()));
        }

        // Save the updates
        return userRepository.save(user);
    }
    

    // Helper method to avoid code duplication
    private GradingSystem parseGradingSystem(String input) {
        if (input != null) {
            try {
                return GradingSystem.valueOf(input);
            } catch(IllegalArgumentException e){
                return GradingSystem.V_SCALE; // Default fallback
            }
        }
        return GradingSystem.V_SCALE; // Default if null
    }
}
