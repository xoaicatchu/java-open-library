package com.example.websocket.service;

import com.example.websocket.dto.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ChatService {

    private final ConcurrentMap<String, List<ChatMessage>> roomHistories = new ConcurrentHashMap<>();

    public void saveMessage(ChatMessage message) {
        String room = message.room() != null ? message.room() : "global";
        roomHistories.computeIfAbsent(room, k -> Collections.synchronizedList(new ArrayList<>()))
                     .add(message);
    }

    public List<ChatMessage> getHistory(String room) {
        return roomHistories.getOrDefault(room, Collections.emptyList());
    }
}
