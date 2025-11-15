package com.dhriti.infotech.controller;

import com.dhriti.infotech.service.AIModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/support-response")
public class SupportResponseDrafterController {

    @Autowired
    private AIModeratorService aiModeratorService;

    @PostMapping("/query")
    public ResponseEntity<String> processSupportResponseDrafter(@RequestParam("customerName") String customerName, @RequestBody Map<String, String> request) {
        String query = request.getOrDefault("query", "").trim();
        String model = request.getOrDefault("model", "openai").trim();

        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body("Query must not be empty.");
        }

        String response = aiModeratorService.processSupportResponseDrafter(customerName, query, model);
        return ResponseEntity.ok(response);
    }

}
