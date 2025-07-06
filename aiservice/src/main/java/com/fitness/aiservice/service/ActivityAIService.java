package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final  GeminiService geminiService;
    public Recommendation generateRecommendation(Activity activity){
        String prompt= createPromptforactivity(activity);
        String airesponse= geminiService.getAnswer(prompt);
        log.info("response from AI: {} ",airesponse);
        return processAIResponse(activity,airesponse);
    }
    private Recommendation processAIResponse(Activity activity,String aiResponse){
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
//            log.info("PARSED Response from AI :{} ",jsonContent);
            JsonNode analysisJson=mapper.readTree(jsonContent);
            JsonNode analysisNode= analysisJson.path("analysis");
            StringBuilder fullAnalysis=new StringBuilder();
            addAnaylsisSection(fullAnalysis,analysisNode,"overall","OverAll:");
            addAnaylsisSection(fullAnalysis,analysisNode,"pace","Pace:");
            addAnaylsisSection(fullAnalysis,analysisNode,"heartRate","Heart Rate:");
            addAnaylsisSection(fullAnalysis,analysisNode,"caloriesBurnt","Calories Burnt:");
            JsonNode ImprovementNode= analysisJson.path("improvements");
            JsonNode suggestionNode=analysisJson.path("suggestions");
            JsonNode safetyguide=analysisJson.path("safety");
            List<String> suggestions = extractListFromJson(suggestionNode, "workout", "description", "No specific suggestions provided");
            List<String> improvements = extractListFromJson(ImprovementNode, "area", "recommendation", "No specific improvements provided");
            List<String> safetyGuideLines= extractSafetyGuideline(safetyguide);
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getGetType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safetyMeasures(safetyGuideLines)
                    .createdAt(LocalDateTime.now()).build();
        }
        catch(Exception e){
            e.printStackTrace();
            return  CreateDefaultRecommendation(activity);
        }
    }

    private Recommendation CreateDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getGetType())
                .recommendation("Unable to generate detailed Service")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safetyMeasures(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hyderated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now()).build();
    }

    private List<String> extractSafetyGuideline(JsonNode safetyNode) {
        List<String>safety=new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(safetyn->safety.add(safetyn.asText()));
        }
        return safety.isEmpty()?Collections.singletonList("Follow general safety Guidelines"):safety;
    }
//    private List<String> extractSuggestions(JsonNode SuggestionNode) {
//        List<String> Suggestions = new ArrayList<>();
//        if (SuggestionNode.isArray()) {
//            SuggestionNode.forEach(improvement -> {
//                String area = improvement.path("workout").asText();
//                String detail = improvement.path("description").asText();
//                Suggestions.add(String.format("%s,%s", area, detail));
//            });
//        }
//        return Suggestions.isEmpty() ? Collections.singletonList("No specific Suggestion provided") : Suggestions;
//    }
//    private List<String> extractImprovements(JsonNode improvementNode) {
//        List<String> improvements = new ArrayList<>();
//        if (improvementNode.isArray()) {
//            improvementNode.forEach(improvement -> {
//                String area = improvement.path("area").asText();
//                String detail = improvement.path("recommendation").asText();
//                improvements.add(String.format("%s,%s", area, detail));
//            });
//        }
//        return improvements.isEmpty() ? Collections.singletonList("No specific improvements provided") : improvements;
//    }
//
    private List<String> extractListFromJson(JsonNode node, String field1, String field2, String defaultMsg) {
        List<String> result = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(item -> {
                String val1 = item.path(field1).asText();
                String val2 = item.path(field2).asText();
                result.add(String.format("%s,%s", val1, val2));
            });
        }
        return result.isEmpty() ? Collections.singletonList(defaultMsg) : result;
    }

    private void addAnaylsisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix).append(analysisNode.path(key).asText()).append("\n\n");
        }
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
