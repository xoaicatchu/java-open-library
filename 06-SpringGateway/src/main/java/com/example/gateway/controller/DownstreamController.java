package com.example.gateway.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Downstream controller dùng để mock các service đích trong demo
 */
@RestController
@RequestMapping("/downstream")
public class DownstreamController {

    @GetMapping("/hello")
    public Map<String, String> hello(@RequestHeader(value = "X-Request-Source", defaultValue = "unknown") String source) {
        return Map.of("message", "Hello from downstream!", "source", source);
    }

    @GetMapping("/new/info")
    public Map<String, String> legacyRewrite() {
        return Map.of("message", "This is the rewritten path!");
    }

    @GetMapping("/flaky/status")
    public ResponseEntity<String> flaky() {
        // Giả lập lỗi để trigger circuit breaker
        return ResponseEntity.status(500).body("Internal Server Error");
    }

    @GetMapping("/limited/data")
    public Map<String, String> limitedData() {
        return Map.of("data", "Some protected data");
    }
}
