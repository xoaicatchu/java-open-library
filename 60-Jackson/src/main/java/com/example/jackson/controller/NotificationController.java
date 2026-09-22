package com.example.jackson.controller;

import com.example.jackson.domain.Notification;
import com.example.jackson.domain.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final Map<UUID, Notification> repository = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        if (notification.getId() == null) {
            notification.setId(UUID.randomUUID());
        }
        repository.put(notification.getId(), notification);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/{id}/summary")
    @JsonView(Views.Public.class)
    public ResponseEntity<Notification> getSummary(@PathVariable UUID id) {
        Notification notification = repository.get(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/{id}/detail")
    @JsonView(Views.Internal.class)
    public ResponseEntity<Notification> getDetail(@PathVariable UUID id) {
        Notification notification = repository.get(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notification);
    }
}
