package com.example.amqp.controller;

import com.example.amqp.dto.OrderNotification;
import com.example.amqp.dto.PaymentNotification;
import com.example.amqp.service.NotificationProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationProducer notificationProducer;

    public NotificationController(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @PostMapping("/order")
    public ResponseEntity<String> sendOrderNotification(@RequestBody OrderNotification notification) {
        notificationProducer.sendOrderNotification(notification);
        return ResponseEntity.ok("Order notification sent");
    }

    @PostMapping("/payment")
    public ResponseEntity<String> sendPaymentNotification(@RequestBody PaymentNotification notification) {
        notificationProducer.sendPaymentNotification(notification);
        return ResponseEntity.ok("Payment notification sent");
    }
}
