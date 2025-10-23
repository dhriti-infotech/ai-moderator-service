package com.dhriti.infotech.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AIModeratorService {

    private static final Logger logger = LoggerFactory.getLogger(AIModeratorService.class);

//    private final ChatClient chatClient;
//
//
//    public AIModeratorService(ChatClient.Builder chatClientBuilder) {
//        this.chatClient = chatClientBuilder.build();
//    }

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public AIModeratorService(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                    @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String processQuery(String query, String model) {
        logger.info("Received query: '{}' for model: '{}'", query, model);

        // 🔒 Simple moderation logic
        String[] restrictedWords = {"word1", "word2", "word3", "word4"};
        String lowerQuery = query.toLowerCase();
        for (String word : restrictedWords) {
            if (lowerQuery.contains(word)) {
                logger.info("Query contains restricted word: {}", word);
                return "Age Restricted Content";
            }
        }

        ChatClient chatClient = selectChatClient(model);

        if (chatClient == null) {
            logger.error("Unsupported model: {}", model);
            return "Unsupported AI model. Please verify configuration.";
        }

        try {
            logger.info("Sending query to {} model...", model);
            String response = chatClient
                    .prompt()
                    .user(query)
                    .call()
                    .content();
            logger.info("Received response: {}", response);
            return response;
        } catch (HttpClientErrorException.NotFound notFoundEx) {
            logger.error("404 Not Found from LLM API for model {}", model, notFoundEx);
            return "Sorry, the AI model or endpoint was not found. Please check configuration.";
        } catch (Exception e) {
            logger.error("Error while calling LLM API for model {}", model, e);
            return "Hi this DhriAI! How can I help you?";
        }
    }

    private ChatClient selectChatClient(String model) {
        if (model == null) return openAiChatClient; // default fallback
        String normalized = model.trim().toLowerCase();

        switch (normalized) {
            case "openai":
                return openAiChatClient;
            case "ollama":
                return ollamaChatClient;
            default:
                logger.warn("Unknown model '{}', defaulting to OpenAI", model);
                return openAiChatClient;
        }
    }
}