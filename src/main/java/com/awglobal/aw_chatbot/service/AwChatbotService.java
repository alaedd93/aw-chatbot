package com.awglobal.aw_chatbot.service;
import com.awglobal.aw_chatbot.model.chatbot.Chatbot;
import com.awglobal.aw_chatbot.model.chatbot.ChatbotRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AwChatbotService {

    private final ChatbotRepository chatbotRepository;

    public AwChatbotService(ChatbotRepository chatbotRepository) {
        this.chatbotRepository = chatbotRepository;
    }

    public Chatbot createChatbot(String name, String provider) {
        Chatbot chatbot = new Chatbot(name, provider);
        return chatbotRepository.save(chatbot);
    }

    public List<Chatbot> getCustomers() {
        return chatbotRepository.findAll();
    }
}
