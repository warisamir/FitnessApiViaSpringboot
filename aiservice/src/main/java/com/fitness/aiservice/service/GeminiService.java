package com.fitness.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import static java.lang.String.*;

@Service
public class GeminiService {
    private final WebClient webClient;
    @Value("${gemini.api.key}")
    private String geminiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;


    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
    }

    public String getAnswer(String question){
        Map<String,Object>requestBody= Map.of("contents",new Object[]{
                   Map.of("parts",new Object[]{
                           Map.of("text",question)
                   })
            }
        );
        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(geminiApiUrl)  // e.g. /v1beta/models/gemini-2.0-flash:generateContent
                        .queryParam("key", geminiKey)
                        .build())
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", geminiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
        }
    }

