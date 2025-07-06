package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final  GeminiService geminiService;
    public String generateRecommendation(Activity activity){
        String prompt= createPromptforactivity(activity);
        String airesponse= geminiService.getAnswer(prompt);
        log.info("response from AI: {} ",airesponse);
        processAIResponse(activity,airesponse);
        return  airesponse;
    }
    private void processAIResponse(Activity activity,String aiResponse){
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0).path("content").path("parts")
                    .get(0).path("text");
            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")  // removes ```json with any linebreak or whitespace
                    .replaceAll("`\\n``", "")              // removes closing ```
                    .trim();
            log.info("PARSED Response from AI :{} ",jsonContent);
        }
        catch(Exception e){
            e.printStackTrace();
        };
    }
    private String createPromptforactivity(Activity activity){
        return String.format("""
        Analyze this fitness  and provide detailed recommendations in this format:
        {
        "analysis":{
        "overall":"Overall analysis here",
        "pace":"Pace Analysis here",
        "heartRate":"Heart Rate analysis here",
        "caloriesBurnt":"Calories analysis here"
        },
        "improvements":[
        {
        "area": "Area name",
        "recommendation":"Detailed Recommendation",
        }
        ],
        "suggestions":[
        {
        "workout":"Workout name",
        "description":"Detailed workout description"
        }
        ],
        "safety":[
        "Safety point 1",
        "Safety point 2",
        ]
        }
        Analyze this activity:
        Activity Type:%s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed Analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
""",activity.getGetType(),
                activity.getDuration(),
                activity.getCaloriesBurnt(),
                activity.getAdditionalMetrics()
                );

    }
}
