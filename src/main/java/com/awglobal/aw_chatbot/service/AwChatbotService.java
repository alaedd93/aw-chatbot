package com.awglobal.aw_chatbot.service;
import com.awglobal.aw_chatbot.dto.ChatbotRequest;
import com.awglobal.aw_chatbot.dto.ChatbotResponse;
import com.awglobal.aw_chatbot.mapper.ChatbotMapper;
import com.awglobal.aw_chatbot.model.chatbot.Chatbot;
import com.awglobal.aw_chatbot.model.chatbot.ChatbotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AwChatbotService {

    private final ChatbotRepository chatbotRepository;
    private final ChatbotMapper chatbotMapper;

    public AwChatbotService(ChatbotRepository chatbotRepository,
                            ChatbotMapper chatbotMapper) {
        this.chatbotRepository = chatbotRepository;
        this.chatbotMapper = chatbotMapper;
    }

    public ChatbotResponse createChatbot(ChatbotRequest chatbotRequest) {
        Chatbot chatbot = new Chatbot(chatbotRequest.name(),
                chatbotRequest.provider());
        Chatbot savedChatbot = chatbotRepository.save(chatbot);
        return chatbotMapper.toResponse(savedChatbot);
    }

    public List<ChatbotResponse> getChatbots() {
        return chatbotRepository.findAll().
                stream().
                map(chatbotMapper::toResponse).
                toList();
    }
}
