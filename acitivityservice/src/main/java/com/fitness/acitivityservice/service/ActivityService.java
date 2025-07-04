package com.fitness.acitivityservice.service;

import com.fitness.acitivityservice.dto.ActivityRequest;
import com.fitness.acitivityservice.dto.ActivityResponse;
import com.fitness.acitivityservice.model.Activity;
import com.fitness.acitivityservice.repository.ActivityRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    public ActivityResponse  trackActivity(ActivityRequest activityRequest){
        boolean isValidUser=userValidationService.validateUser(activityRequest.getUserId());
            if(!isValidUser){
                throw new RuntimeException("Invalid User: "+ activityRequest.getUserId());
        }
        Activity activity= Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .duration(activityRequest.getDuration())
                .caloriesBurnt(activityRequest.getCaloriesBurnt())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();
        Activity savedActivity= activityRepository.save(activity);
        return mapToResponse(savedActivity);
    }
    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response= new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurnt(activity.getCaloriesBurnt());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }
}

