package com.dhriti.infotech.service;

//import jakarta.annotation.Resource;
import com.dhriti.infotech.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AIModeratorService {

    private static final Logger logger = LoggerFactory.getLogger(AIModeratorService.class);


    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public AIModeratorService(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                    @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @Value("classpath:/promptTemplates/supportResponseTemplate.st")
    Resource supportResponseTemplateResource;

    @Value("classpath:/PromptStuffing/DIPolicies.st")
    Resource diPoliciesTemplateResource;


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
                    .advisors(new TokenUsageAuditAdvisor())
                  /*  system role is getting used here, it is restricted as specified role only to respond
                     Here the system role is defined to limit the AI's responses to HR-related queries only.
                     Removed the below part to use default system prompt from configuration
                    .system("""
                            You are an HR assistant. Answer concisely and professionally,\s
                            and your role is to provide HR queries related information.
                            """)

                   */

                    /*
                    Use system apart from default if we want to override for specific queries

                    .system("You are an Internal IT support assistant. Answer concisely and professionally,\s"+
                            "and your role is to provide IT support related information only.")

                     */
                    .system(diPoliciesTemplateResource)
                    .user(query) // user role is getting used here, others are system etc
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

    public String processSupportResponseDrafter(String name, String query, String model) {
        String prompt = String.format("Draft a professional support response for %s regarding the following query: %s", name, query);
        ChatClient chatClient = selectChatClient(model);
        return chatClient
                .prompt()
                .system("""
                        You are a professional customer service assistant which helps drafting email
                        responses to improve the productivity of the customer support team
                        """)
                .user(promptTemplateSpec ->
                        promptTemplateSpec.text(supportResponseTemplateResource)
                                .param("customerName", name)
                                .param("customerMessage", query))
                .call().content();
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