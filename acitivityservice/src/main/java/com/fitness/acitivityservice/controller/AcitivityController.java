package com.fitness.acitivityservice.controller;

import com.fitness.acitivityservice.dto.ActivityRequest;
import com.fitness.acitivityservice.dto.ActivityResponse;
import com.fitness.acitivityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/activity")
public class AcitivityController {
    @Autowired
    private  ActivityService activityService;
    @PostMapping("/")
    public ResponseEntity<ActivityResponse>trackActivity(@RequestBody ActivityRequest activityRequest){
        return ResponseEntity.ok(activityService.trackActivity(activityRequest));
    }

}
