package com.dhriti.infotech.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Collections;
import java.util.Map;

@Service
public class AIModeratorService {
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    // The below one is of Jyoti Account
    private static final String API_KEY = "AIzaSyCes-rShFVk-wrBT1YW9bgLG-K6WbPEo1U";

    public String processQuery(String query) {

        if("DhriAI".contains(query.trim())) {
            return "Hi this DhriAI! How can I help you?";
        }
        // Age-restricted keywords (case-insensitive)
        String[] restrictedWords = {"sex", "penis", "vagina", "porn"};
        String lowerQuery = query.toLowerCase();
        for (String word : restrictedWords) {
            if (lowerQuery.contains(word)) {
                return "Age Restricted Content";
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", API_KEY);

        Map<String, Object> body = Collections.singletonMap("contents", Collections.singletonList(
            Collections.singletonMap("parts", Collections.singletonList(
                Collections.singletonMap("text", query)
            ))
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(GEMINI_API_URL, request, Map.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return "Hi this DhriAI! How can I help you?";
            }
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return "Error: Gemini API endpoint or model not found. Check your API key and endpoint.";
            }
            if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return "Hi this DhriAI! How can I help you?";
            }
            return "Error: " + response.getStatusCode();
        }

        Map data = response.getBody();
        try {
            Object candidates = data.get("candidates");
            if (candidates instanceof java.util.List && !((java.util.List) candidates).isEmpty()) {
                Map candidate = (Map) ((java.util.List) candidates).get(0);
                Map content = (Map) candidate.get("content");
                java.util.List parts = (java.util.List) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map part = (Map) parts.get(0);
                    Object text = part.get("text");
                    return text != null ? text.toString() : "Sorry, I couldn't get a response.";
                }
            }
        } catch (Exception e) {
            return "Sorry, I couldn't get a response.";
        }
        return "Sorry, I couldn't get a response.";
    }
}
