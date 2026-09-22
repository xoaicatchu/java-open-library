package com.example.authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/info")
    public ResponseEntity<Map<String, String>> getPublicInfo() {
        return ResponseEntity.ok(Map.of(
            "name", "Spring Authorization Server",
            "status", "RUNNING",
            "tokenEndpoint", "/oauth2/token",
            "discoveryEndpoint", "/.well-known/openid-configuration"
        ));
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages() {
        return ResponseEntity.ok(Map.of(
            "message", "Hello from OAuth2 Protected Resource!",
            "scopeRequired", "message.read"
        ));
    }
}
