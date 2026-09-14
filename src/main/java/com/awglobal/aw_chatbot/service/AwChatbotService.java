package com.awglobal.aw_chatbot.service;
import com.awglobal.aw_chatbot.model.Chatbot;
import org.springframework.stereotype.Service;
import com.awglobal.aw_chatbot.dto.ChatbotRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AwChatbotService {

    private final List<Chatbot> chatbots = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public AwChatbotService(){
        chatbots.add( new Chatbot(
                idGenerator.getAndIncrement(),
                "Bedrock",
                "AWS"
        ));
    }

    public List<Chatbot> getAllChatbots() {
        return  chatbots;
    }

    public Chatbot getChatbotById(Long id) {
        return chatbots.stream()
                .filter(chatbot -> chatbot.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chatbot with id " + id + " was not found"
                ));
    }

    public Chatbot createChatbot(ChatbotRequest request) {
        Chatbot chatbot = new Chatbot(
                idGenerator.getAndIncrement(),
                request.name(),
                request.provider()
        );

        chatbots.add(chatbot);
        return chatbot;
    }

    public Chatbot updateChatbot(Long id, ChatbotRequest request) {
        Chatbot existingChatbot = getChatbotById(id);

        Chatbot updatedChatbot = new Chatbot(
                existingChatbot.id(),
                request.name(),
                request.provider()
        );

        chatbots.remove(existingChatbot);
        chatbots.add(updatedChatbot);

        return updatedChatbot;
    }

    public void deleteChatbot(Long id) {
        Chatbot chatbot = getChatbotById(id);
        chatbots.remove(chatbot);
    }
}
