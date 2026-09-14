package com.awglobal.aw_chatbot.controller;

import com.awglobal.aw_chatbot.dto.ChatbotRequest;
import com.awglobal.aw_chatbot.model.chatbot.Chatbot;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.awglobal.aw_chatbot.service.AwChatbotService;

import java.util.List;

@RestController
@RequestMapping("/api/chatbots")
public class AwChatbotController {

    private final AwChatbotService awChatbotService;

    public AwChatbotController(AwChatbotService awChatbotService){
        this.awChatbotService = awChatbotService;
    }

    @GetMapping
    public List<Chatbot> getCustomers() {
        return awChatbotService.getCustomers();
    }

    @PostMapping
    public Chatbot createCustomer(@RequestBody Chatbot chatbot) {
        return awChatbotService.createChatbot(
                chatbot.getName(),
                chatbot.getProvider()
        );
    }
}
