package com.fwcoding.climbing_app.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fwcoding.climbing_app.dto.ClimbRequest;
import com.fwcoding.climbing_app.dto.ClimbResponse;
import com.fwcoding.climbing_app.service.ClimbService;

@RestController //Tells spring this class Handles HttpRequests
@RequestMapping("/api/climbs") //Base URL: All methods starts with /api/climbs
public class ClimbController {

    private final ClimbService climbService;

    public ClimbController(ClimbService climbSerivce){
        this.climbService = climbSerivce;
    }

    // 1. Log a new climb
    // URL: POST /api/climbs
    // Response Body contains three things: Body, status code, headers(meta data like content type: application/json or custom tokens)
    @PostMapping // only runs on POST requets
    public ResponseEntity<ClimbResponse> logClimb(@RequestBody ClimbRequest request){ //Request Body takes json and turn into Java object
        ClimbResponse response = climbService.logClimb(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //2. Get a Climb by id
    @GetMapping("/{id}")
    public ResponseEntity<ClimbResponse> getClimb(@PathVariable Long id){
        ClimbResponse response = climbService.getClimbById(id);
        return ResponseEntity.ok(response);
    }

    // 3. Update a Climb
    @PutMapping("/{id}")
    public ResponseEntity<ClimbResponse> updateClimb(@PathVariable Long id, @RequestBody ClimbRequest request){
        ClimbResponse response = climbService.updateClimb(id, request);
        return ResponseEntity.ok(response);
    }

    // 3. Delete a Climb (Undo)
    // URL: DELETE /api/climbs/{id}  (e.g., /api/climbs/55)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClimb(@PathVariable Long id){
        climbService.deleteClimb(id);
        // Return 204 No Content (Standard for successful deletion)
        return ResponseEntity.noContent().build();
    }

}
