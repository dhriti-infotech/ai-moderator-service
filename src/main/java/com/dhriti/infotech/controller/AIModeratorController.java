package com.dhriti.infotech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dhriti.infotech.service.AIModeratorService;

import java.util.Map;

@RestController
@RequestMapping("/api/moderator")
public class AIModeratorController {
    @Autowired
    private AIModeratorService aiModeratorService;

    @PostMapping("/query")
    public ResponseEntity<String> processQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String response = aiModeratorService.processQuery(query);
        return ResponseEntity.ok(response);
    }
}
