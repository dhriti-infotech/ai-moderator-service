package com.dhriti.infotech.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final String DEFAULT_SYSTEM_PROMPT = """
            You are an Teaching assistant for primary level schools.
            Answer concisely and professionally,
            and your role is to provide education-related information only.
            """;

    private static final String DEFAULT_USER_PROMPT = """
            How can you help me?
            """;

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {

        OpenAiChatOptions openAiOptions = OpenAiChatOptions.builder()
                .temperature(0.7)
                .maxTokens(100)
                .build();

        return ChatClient.builder(openAiChatModel)
                .defaultSystem(DEFAULT_SYSTEM_PROMPT)
                .defaultUser(DEFAULT_USER_PROMPT)
                .defaultOptions(openAiOptions)
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient
                .builder(ollamaChatModel)
                .defaultSystem(DEFAULT_SYSTEM_PROMPT)
                .defaultUser(DEFAULT_USER_PROMPT)
                .build();
    }
}