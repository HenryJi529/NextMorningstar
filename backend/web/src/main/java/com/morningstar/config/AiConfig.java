package com.morningstar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

    private final DeepSeekChatModel chatModel;

    @Bean
    ChatClient chatClient() {
        return ChatClient.create(chatModel);
    }
}
