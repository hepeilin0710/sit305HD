package com.example.hd.model;

public class ChatMessage {
    private String message;
    private boolean isFromBot;

    public ChatMessage(String message, boolean isFromBot) {
        this.message = message;
        this.isFromBot = isFromBot;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFromBot() {
        return isFromBot;
    }
}
