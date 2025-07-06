package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityMessageListener {
    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;
    @RabbitListener(queues = "activity.queue")
    public void processAcitivity(Activity activity){
        log.info("received activity for procesing :{} ",activity.getId());
        log.info("Generated recommendation ",activityAIService.generateRecommendation(activity));
        Recommendation recommendation= activityAIService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }
}

