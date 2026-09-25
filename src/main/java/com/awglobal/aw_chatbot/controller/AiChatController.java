package com.awglobal.aw_chatbot.controller;


import com.awglobal.aw_chatbot.dto.AiChatRequest;
import com.awglobal.aw_chatbot.dto.AiChatResponse;
import com.awglobal.aw_chatbot.service.AiChatService;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping
    public AiChatResponse chat(
            @Valid @RequestBody AiChatRequest request, @AuthenticationPrincipal Jwt jwt) {

        String memoryConversationId = jwt.getSubject()
                + ":"
                + request.conversationId();

        String answer =
                aiChatService.ask(request.message(), memoryConversationId);

        return new AiChatResponse(answer);
    }


}
