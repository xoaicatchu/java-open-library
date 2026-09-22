package com.example.websocket;

import com.example.websocket.config.WebSocketConfig;
import com.example.websocket.controller.ChatController;
import com.example.websocket.dto.ChatMessage;
import com.example.websocket.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpringWebSocketApplicationTests {

    @Autowired
    private ChatController chatController;

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertThat(chatController).isNotNull();
        assertThat(webSocketConfig).isNotNull();
        assertThat(messagingTemplate).isNotNull();
    }

    @Test
    void testChatServiceSaveAndGetHistory() {
        ChatMessage msg = new ChatMessage("Alice", "Hello Bob", ChatMessage.MessageType.CHAT, "Bob", "room1");
        chatService.saveMessage(msg);

        assertThat(chatService.getHistory("room1")).hasSize(1);
        assertThat(chatService.getHistory("room1").get(0).content()).isEqualTo("Hello Bob");
        assertThat(chatService.getHistory("unknown")).isEmpty();
    }

    @Test
    void testChatMessageSerialization() throws Exception {
        ChatMessage msg = new ChatMessage("Charlie", "Hi", ChatMessage.MessageType.JOIN, null, null);
        String json = objectMapper.writeValueAsString(msg);
        assertThat(json).contains("Charlie", "JOIN", "Hi");

        ChatMessage deserialized = objectMapper.readValue(json, ChatMessage.class);
        assertThat(deserialized.sender()).isEqualTo("Charlie");
        assertThat(deserialized.type()).isEqualTo(ChatMessage.MessageType.JOIN);
    }

    @Test
    void testRestEndpointPushMessageBroadcast() throws Exception {
        ChatMessage msg = new ChatMessage("Admin", "Server down in 5 mins", ChatMessage.MessageType.CHAT, null, "alerts");

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(msg)))
                .andExpect(status().isOk());

        assertThat(chatService.getHistory("alerts")).isNotEmpty();
        assertThat(chatService.getHistory("alerts").get(0).content()).isEqualTo("Server down in 5 mins");
    }

    @Test
    void testRestEndpointPushMessagePrivate() throws Exception {
        ChatMessage msg = new ChatMessage("Admin", "You are promoted", ChatMessage.MessageType.PRIVATE, "Alice", null);

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(msg)))
                .andExpect(status().isOk());

        assertThat(chatService.getHistory("global")).isNotEmpty();
        boolean found = chatService.getHistory("global").stream()
                .anyMatch(m -> "You are promoted".equals(m.content()));
        assertThat(found).isTrue();
    }

    @Test
    void testChatControllerSendMessage() {
        ChatMessage msg = new ChatMessage("Dave", "Hello public", ChatMessage.MessageType.CHAT, null, "public");
        ChatMessage result = chatController.sendMessage(msg);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Hello public");
        assertThat(chatService.getHistory("public")).hasSize(1);
    }
}
