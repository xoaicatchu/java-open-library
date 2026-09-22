package com.example.amqp.service;

import com.example.amqp.config.RabbitConfig;
import com.example.amqp.dto.OrderNotification;
import com.example.amqp.dto.PaymentNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = RabbitConfig.ORDER_EMAIL_QUEUE)
    public void processOrderEmail(OrderNotification notification) {
        log.info("Nhận email cho đơn hàng: {}", notification);
        if (notification.orderId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null, simulating failure for DLQ");
        }
    }

    @RabbitListener(queues = RabbitConfig.ORDER_SMS_QUEUE)
    public void processOrderSms(OrderNotification notification) {
        log.info("Nhận SMS cho đơn hàng: {}", notification);
    }

    @RabbitListener(queues = RabbitConfig.PAYMENT_QUEUE)
    @SendTo
    public String processPayment(PaymentNotification notification) {
        log.info("Xử lý thanh toán: {}", notification);
        return "Processed payment " + notification.paymentId();
    }
    
    @RabbitListener(queues = RabbitConfig.DLQ)
    public void processDeadLetterQueue(Object message) {
        log.warn("Nhận tin nhắn bị lỗi từ DLQ: {}", message);
    }
}
