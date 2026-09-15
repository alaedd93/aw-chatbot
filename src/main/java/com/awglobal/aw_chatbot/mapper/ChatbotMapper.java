package com.awglobal.aw_chatbot.mapper;

import com.awglobal.aw_chatbot.dto.ChatbotResponse;
import com.awglobal.aw_chatbot.model.chatbot.Chatbot;
import org.springframework.stereotype.Component;

@Component
public class ChatbotMapper {

    public ChatbotResponse toResponse(Chatbot chatbot){
        return new ChatbotResponse(chatbot.getId(),
                chatbot.getName(),
                chatbot.getProvider());
    }
}
