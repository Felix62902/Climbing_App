package com.fwcoding.climbing_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fwcoding.climbing_app.repository.WallRepository;

// This service is used to interact with Gym Repo and Wall Repo, Main focus is to manage locations and layout
// doesnt implement interface for now but may need to in the future if it gets complicated
// Required Services: CRUD 
@Service
public class GymService {
    private final GymRepository gymRepo;
    private final WallRepository wallRepo;

    public GymService(GymRepository gymRepo, WallRepository wallRepo){
        this.gymRepo = gymRepo;
        this.wallRepo = wallRepo;
    }

    // public List<Gym> getAllGyms(){
    //     gymRepo.findAll();
    // }

    
}
