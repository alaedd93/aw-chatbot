package com.awglobal.aw_chatbot.controller;

import com.awglobal.aw_chatbot.model.Chatbot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.awglobal.aw_chatbot.service.AwChatbotService;

@RestController
@RequestMapping("/api/chatbot")
public class AwChatbotController {

    private final AwChatbotService awChatbotService;

    public AwChatbotController(AwChatbotService awChatbotService){
        this.awChatbotService = awChatbotService;
    }
    @GetMapping
    public Chatbot getChatbotName() {
        return awChatbotService.getChatbotName();
    }
}
