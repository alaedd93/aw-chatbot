package com.awglobal.aw_chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are AW Assistant.

                        You are a helpful enterprise AI assistant.
                        Provide clear, accurate and concise answers.
                        If you do not know something, say that you do not know.
                        """)
                .build();
    }

    public String ask(String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}