package com.awglobal.aw_chatbot.service;
import com.awglobal.aw_chatbot.model.Chatbot;
import org.springframework.stereotype.Service;

@Service
public class AwChatbotService {

    public Chatbot getChatbotName() {
        return new Chatbot(1L,
                "Bedrock",
                "AWS");
    }
}
