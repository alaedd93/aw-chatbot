package com.awglobal.aw_chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are AW Assistant.

                        You are a helpful enterprise AI assistant.
                        Provide clear, accurate and concise answers.
                        If you do not know something, say that you do not know.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .build()
                )
                .build();
    }

    public String ask(String message, String conversationId) {

        return chatClient
                .prompt()
                .user(message)
                .advisors(advisor ->
                        advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        ))
                .call()
                .content();
    }
}