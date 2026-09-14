package com.awglobal.aw_chatbot.model.chatbot;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chatbots")
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String provider;

    protected Chatbot() {
        // Requis par JPA
    }

    public Chatbot(String name, String provider) {
        this.name = name;
        this.provider = provider;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getProvider() {
        return provider;
    }
}