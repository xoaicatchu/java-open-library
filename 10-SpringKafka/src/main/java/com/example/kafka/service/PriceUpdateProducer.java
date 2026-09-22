package com.example.kafka.service;

import com.example.kafka.dto.PriceUpdateEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class PriceUpdateProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PriceUpdateProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPriceUpdate(String topic, PriceUpdateEvent event) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic,
                null,
                event.timestamp().toEpochMilli(),
                String.valueOf(event.productId()),
                event
        );
        record.headers().add(KafkaHeaders.KEY, String.valueOf(event.productId()).getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);
    }
}
