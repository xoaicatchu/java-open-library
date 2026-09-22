package com.example.websocket.dto;

public record ChatMessage(
    String sender,
    String content,
    MessageType type,
    String recipient,
    String room
) {
    public enum MessageType {
        CHAT, JOIN, LEAVE, PRIVATE
    }
}
