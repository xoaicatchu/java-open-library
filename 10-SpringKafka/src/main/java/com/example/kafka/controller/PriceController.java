package com.example.kafka.controller;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.dto.PriceUpdateEvent;
import com.example.kafka.entity.ProductPrice;
import com.example.kafka.repository.ProductPriceRepository;
import com.example.kafka.service.PriceUpdateProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceUpdateProducer producer;
    private final ProductPriceRepository repository;

    public PriceController(PriceUpdateProducer producer, ProductPriceRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Void> sendPriceUpdate(@RequestBody PriceUpdateEvent event) {
        // Use current time if timestamp is not provided
        if (event.timestamp() == null) {
            event = new PriceUpdateEvent(event.productId(), event.oldPrice(), event.newPrice(), Instant.now());
        }
        producer.sendPriceUpdate(KafkaConfig.TOPIC_UPDATE, event);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> sendPriceUpdateBatch(@RequestBody List<PriceUpdateEvent> events) {
        for (PriceUpdateEvent event : events) {
            if (event.timestamp() == null) {
                event = new PriceUpdateEvent(event.productId(), event.oldPrice(), event.newPrice(), Instant.now());
            }
            producer.sendPriceUpdate(KafkaConfig.TOPIC_BATCH, event);
        }
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductPrice> getPrice(@PathVariable Long productId) {
        return repository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductPrice>> getAllPrices() {
        return ResponseEntity.ok(repository.findAll());
    }
}
