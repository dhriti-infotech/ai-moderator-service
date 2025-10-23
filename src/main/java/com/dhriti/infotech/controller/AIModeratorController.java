package com.dhriti.infotech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dhriti.infotech.service.AIModeratorService;
import java.util.Map;

@RestController
@RequestMapping("/api/moderator")
public class AIModeratorController {

    @Autowired
    private AIModeratorService aiModeratorService;

    /**
     * POST /api/moderator/query
     * Accepts a query and optional model name, routes it to the appropriate AI model.
     *
     * Example payload:
     * {
     *   "query": "What is the capital of France?",
     *   "model": "ollama"
     * }
     */
    @PostMapping("/query")
    public ResponseEntity<String> processQuery(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("query", "").trim();
        String model = request.getOrDefault("model", "openai").trim();

        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body("Query must not be empty.");
        }

        String response = aiModeratorService.processQuery(query, model);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/moderator/health
     * Simple health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("AI Moderator Service is running ✅");
    }
}
