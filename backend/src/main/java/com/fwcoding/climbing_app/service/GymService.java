package com.fwcoding.climbing_app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fwcoding.climbing_app.dto.GymRequest;
import com.fwcoding.climbing_app.dto.GymResponse;
import com.fwcoding.climbing_app.dto.GymRequest.GymGradeRequest;
import com.fwcoding.climbing_app.dto.GymRequest.WallRequest;
import com.fwcoding.climbing_app.enums.GradingSystem;
import com.fwcoding.climbing_app.enums.WallType;
import com.fwcoding.climbing_app.model.Gym;
import com.fwcoding.climbing_app.model.GymGrade;
import com.fwcoding.climbing_app.model.Wall;
import com.fwcoding.climbing_app.repository.GymRepository;

// This service is used to interact with Gym Repo and Wall Repo, Main focus is to manage locations and layout
// doesnt implement interface for now but may need to in the future if it gets complicated
// Required Services: CRUD 
@Service
public class GymService {
    private final GymRepository gymRepo;

    public GymService(GymRepository gymRepo){
        this.gymRepo = gymRepo;
    }

    // Create
    public GymResponse createGym(GymRequest request){
        Gym gym = new Gym();
        gym.setName(request.getName());
        gym.setLocation(request.getLocation());
        gym.setPhotoUrl(request.getPhotoUrl());
        try {
            gym.setGradingSystem(GradingSystem.valueOf(request.getGradingSystem()));
        } catch (Exception e) {
            gym.setGradingSystem(GradingSystem.V_SCALE); // Default
        }
        // gym.setGymGrades(null);
        
        //Handle walls
        if (request.getWalls() != null){
            List<Wall> wallEntities = new ArrayList<>();
            for (WallRequest wDto : request.getWalls()) {
                Wall wall = new Wall();
                wall.setName(wDto.getName());
                wall.setPhotoUrl(wDto.getPhotoUrl());
                try {
                    wall.setDefaultType(WallType.valueOf(wDto.getDefaultType()));
                } catch (Exception e) {
                    System.out.println("Error when setting wall Default Type! " + e);
                }
                wall.setGym(gym); // Link Parent
                wallEntities.add(wall);
            }
            gym.setWalls(wallEntities);
        }

        if (request.getGymGrades() != null){
            List<GymGrade> gradeEntities = new ArrayList<>();
            for (GymGradeRequest ggDTO: request.getGymGrades()){
                GymGrade gymGrade = new GymGrade();
                gymGrade.setColorHex(ggDTO.getColorHex());
                gymGrade.setLabel(ggDTO.getLabel());
                gymGrade.setSortOrder(ggDTO.getSortOrder());
                gymGrade.setGym(gym);
                gradeEntities.add(gymGrade);
            }
            gym.setGymGrades(gradeEntities);
        }

        boolean hasWalls = gym.getWalls() != null && !gym.getWalls().isEmpty();
        boolean hasGrades = gym.getGymGrades() != null && !gym.getGymGrades().isEmpty();
        gym.setSetupComplete(hasWalls && hasGrades);

        Gym savedGym = gymRepo.save(gym);
        return mapToResponse(savedGym);
    }

    // Get All (Returning DTOs)
    public List<GymResponse> getAllGyms(){
        return gymRepo.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- Helper: Entity -> DTO ---
    private GymResponse mapToResponse(Gym gym) {
        GymResponse response = new GymResponse();
        response.setId(gym.getId()); 
        response.setName(gym.getName());
        response.setLocation(gym.getLocation());
        response.setPhotoUrl(gym.getPhotoUrl());
        response.setGradingSystem(gym.getGradingSystem());
        response.setSetupComplete(gym.isSetupComplete());

        //Convert Wall Entities -> WallResponse DTOs
        if (gym.getWalls() != null) {
            List<GymResponse.WallResponse> wallDtos = gym.getWalls().stream().map(wall -> {
                GymResponse.WallResponse dto = new GymResponse.WallResponse();
                dto.setId(wall.getId()); // Ensure Wall entity has getId()
                dto.setName(wall.getName());
                dto.setPhotoUrl(wall.getPhotoUrl());
                if (wall.getDefaultType() != null) {
                    dto.setDefaultType(wall.getDefaultType().name());
                }
                return dto;
            }).collect(Collectors.toList());
            
            response.setWalls(wallDtos);
        }

        //  Convert Grade Entities -> GymGradeResponse DTOs
        if (gym.getGymGrades() != null) {
            List<GymResponse.GymGradeResponse> gradeDtos = gym.getGymGrades().stream().map(grade -> {
                GymResponse.GymGradeResponse dto = new GymResponse.GymGradeResponse();
                dto.setId(grade.getId());
                dto.setLabel(grade.getLabel());
                dto.setColorHex(grade.getColorHex());
                dto.setSortOrder(grade.getSortOrder());
                return dto;
            }).collect(Collectors.toList());
            
            response.setGymGrades(gradeDtos);
        }
        return response;
    }
    
}
