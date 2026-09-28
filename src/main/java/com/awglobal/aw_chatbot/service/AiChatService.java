package com.awglobal.aw_chatbot.service;

import com.awglobal.aw_chatbot.dto.ChatHistoryMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory;

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

        this.chatMemory = chatMemory;
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


    public List<ChatHistoryMessage> getHistory(
            String conversationId
    ){
        return chatMemory.get(conversationId)
                .stream()
                .filter( message ->
                        message.getMessageType() == MessageType.USER
                ||
                        message.getMessageType() == MessageType.ASSISTANT
                )
                .filter( message ->
                        message.getText() != null
                )
                .map(message ->
                        new ChatHistoryMessage(
                                message.getMessageType()
                                        .name()
                                        .toLowerCase(Locale.ROOT),
                                message.getText()
                        )
                )
                .toList();


    }


}