package com.awglobal.aw_chatbot.controller;

import com.awglobal.aw_chatbot.dto.ChatbotRequest;
import com.awglobal.aw_chatbot.model.Chatbot;
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
    public List<Chatbot> getAllChatbots() {
        return awChatbotService.getAllChatbots();
    }

    @GetMapping("/{id}")
    public Chatbot getChatbotById(@PathVariable Long id) {
        return awChatbotService.getChatbotById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Chatbot createChatbot(@RequestBody ChatbotRequest request) {
        return awChatbotService.createChatbot(request);
    }

    @PutMapping("/{id}")
    public Chatbot updateChatbot(
            @PathVariable Long id,
            @RequestBody ChatbotRequest request
    ) {
        return awChatbotService.updateChatbot(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChatbot(@PathVariable Long id) {
        awChatbotService.deleteChatbot(id);
    }
}
