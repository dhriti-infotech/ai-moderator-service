package com.dhriti.infotech.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final String DEFAULT_SYSTEM_PROMPT = """
            You are an Teaching assistant for primary level schools.\s
            Answer concisely and professionally,\s
            and your role is to provide education-related information only.
           \s""";

    private static final String DEFAULT_USER_PROMPT = """
            How can you help me?
            """;

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient
                .builder(openAiChatModel)
                .defaultSystem(DEFAULT_SYSTEM_PROMPT)
                .defaultUser(DEFAULT_USER_PROMPT)
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