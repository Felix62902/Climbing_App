package com.fwcoding.climbing_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importing * is fine, or list them out

import com.fwcoding.climbing_app.dto.UserProfileResponse;
import com.fwcoding.climbing_app.dto.UserRegistrationRequest;
import com.fwcoding.climbing_app.dto.UserPreferenceRequest;   // Make sure you have this!
import com.fwcoding.climbing_app.dto.UserPreferenceResponse;  // Make sure you have this!
import com.fwcoding.climbing_app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // URL: POST /api/users/register
    @PostMapping("/register")
    // CHANGED: Return UserProfileResponse (Safe), not User (Unsafe)
    public ResponseEntity<UserProfileResponse> register(@RequestBody UserRegistrationRequest request){
        UserProfileResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // URL: GET /api/users/{id}/profile
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id){
        return ResponseEntity.ok(userService.getProfile(id));
    }

    // URL: PUT /api/users/{id}/profile
    @PutMapping("/{id}/profile")
    // CHANGED: Return UserProfileResponse
    public ResponseEntity<UserProfileResponse> updateProfile(
        @PathVariable Long id, 
        @RequestBody UserRegistrationRequest request // Reusing Reg request is okay for now
    ){
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    // URL: PUT /api/users/{id}/preference
    @PutMapping("/{id}/preference")
    public ResponseEntity<UserPreferenceResponse> updatePreferences(
        @PathVariable Long id, 
        @RequestBody UserPreferenceRequest request
    ){
        return ResponseEntity.ok(userService.updatePreferences(id, request));
    }

    //Delete user not necessary yet
}