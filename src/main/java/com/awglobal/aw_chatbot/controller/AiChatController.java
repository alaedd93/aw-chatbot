package com.awglobal.aw_chatbot.controller;


import com.awglobal.aw_chatbot.dto.AiChatRequest;
import com.awglobal.aw_chatbot.dto.AiChatResponse;
import com.awglobal.aw_chatbot.dto.ChatHistoryMessage;
import com.awglobal.aw_chatbot.service.AiChatService;
import com.awglobal.aw_chatbot.service.ConversationKeys;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

        String memoryConversationId =
                ConversationKeys.forUser(
                        jwt,
                        request.conversationId()
                );

        String answer =
                aiChatService.ask(request.message(), memoryConversationId);

        return new AiChatResponse(answer);
    }


    @GetMapping("/history")
    public List<ChatHistoryMessage> getHistory(
            @RequestParam
            UUID conversationId,

            @AuthenticationPrincipal
            Jwt jwt
    ){
        String memoryConversationId =
                ConversationKeys.forUser(
                        jwt,
                        conversationId
                );

        return aiChatService.getHistory(
                memoryConversationId
        );

    }


}
