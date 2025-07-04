package com.fitness.acitivityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationService {
    private final WebClient userServiceWebClient;
    public boolean validateUser(String userId){
        log.info("Calling User Valdiation Api for userID:{} "+userId);
        try {
            return Boolean.TRUE.equals(userServiceWebClient.get().uri("api/users/{userId}/validate", userId)
                    .retrieve().
                    bodyToMono(Boolean.class)
                    .block());
        }
        catch (WebClientResponseException ex){
            if(ex.getStatusCode()== HttpStatus.NOT_FOUND){
                throw new RuntimeException("User not found: "+ userId);
            } else if (ex.getStatusCode()== HttpStatus.BAD_REQUEST) {
                throw  new RuntimeException("invalid request: "+userId);
            }
        }
        return false;
    }
}
