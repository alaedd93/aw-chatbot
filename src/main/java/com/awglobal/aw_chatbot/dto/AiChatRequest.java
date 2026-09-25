package com.awglobal.aw_chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AiChatRequest(
        @NotNull(message = "Conversation ID is required")
        UUID conversationId,

        @NotBlank(message = "Message must not be blank")
        @Size(
                max = 4000,
                message = "Message must not exceed 4000 characters"
        )
        String message

) {
}
