package com.awglobal.aw_chatbot.config;

import com.awglobal.aw_chatbot.service.AiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(
        name = "app.ai.smoke-test.enabled",
        havingValue = "true"
)
public class BedrockSmokeTestRunner implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(BedrockSmokeTestRunner.class);

    private final AiChatService aiChatService;

    public BedrockSmokeTestRunner(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @Override
    public void run(ApplicationArguments args) {

        log.info("Sending smoke-test request to Amazon Bedrock...");
        String userId = "user0";
        UUID conversationId = UUID.randomUUID();
        String memoryConversationId = userId + ":" + conversationId;

        String response = aiChatService.ask(
                "Say hello from my Spring Boot application.",
                memoryConversationId
        );

        log.info("Bedrock response: {}", response);
    }
}