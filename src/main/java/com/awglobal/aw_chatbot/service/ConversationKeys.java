package com.awglobal.aw_chatbot.service;

import org.springframework.security.oauth2.jwt.Jwt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ConversationKeys {

    private ConversationKeys() {
    }

    public static String forUser(
            Jwt jwt,
            UUID conversationId
    ) {

        String input =
                jwt.getIssuer().toString()
                        + ":"
                        + jwt.getSubject()
                        + ":"
                        + conversationId;

        return UUID.nameUUIDFromBytes(
                input.getBytes(StandardCharsets.UTF_8)
        ).toString();
    }
}