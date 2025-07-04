package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityMessageListener {
    @RabbitListener(queues = "activity.queue")
    public void processAcitivity(Activity activity){
        log.info("received activity for procesing :{} ",activity.getId());
    }
}
