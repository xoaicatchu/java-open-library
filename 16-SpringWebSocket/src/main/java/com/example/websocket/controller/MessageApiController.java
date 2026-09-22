package com.example.websocket.controller;

import com.example.websocket.dto.ChatMessage;
import com.example.websocket.service.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public MessageApiController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @PostMapping
    public void pushMessage(@RequestBody ChatMessage message) {
        chatService.saveMessage(message);
        if (message.recipient() != null) {
            messagingTemplate.convertAndSendToUser(message.recipient(), "/queue/private", message);
        } else {
            String topic = message.room() != null ? "/topic/" + message.room() : "/topic/public";
            messagingTemplate.convertAndSend(topic, message);
        }
    }
}
