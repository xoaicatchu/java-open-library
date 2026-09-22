package com.example.kafka.service;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.dto.PriceUpdateEvent;
import com.example.kafka.entity.ProductPrice;
import com.example.kafka.repository.ProductPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(PriceUpdateConsumer.class);
    private final ProductPriceRepository repository;

    public PriceUpdateConsumer(ProductPriceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @KafkaListener(topics = KafkaConfig.TOPIC_UPDATE, groupId = "price-group")
    public void consumeSingle(
            @Payload PriceUpdateEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            Acknowledgment acknowledgment) {
        
        log.info("Received single update for product: {} from partition {}", event.productId(), partition);
        
        if (event.newPrice().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid price for product {}", event.productId());
            throw new IllegalArgumentException("Price cannot be negative! Triggering DLT...");
        }

        ProductPrice price = new ProductPrice(event.productId(), event.newPrice(), event.timestamp());
        repository.save(price);
        
        // Manual ack
        acknowledgment.acknowledge();
    }

    @Transactional
    @KafkaListener(topics = KafkaConfig.TOPIC_BATCH, groupId = "price-batch-group", containerFactory = "batchFactory")
    public void consumeBatch(
            @Payload List<PriceUpdateEvent> events,
            Acknowledgment acknowledgment) {
        
        log.info("Received batch of {} updates", events.size());
        
        List<ProductPrice> entities = events.stream()
                .map(e -> new ProductPrice(e.productId(), e.newPrice(), e.timestamp()))
                .toList();
                
        repository.saveAll(entities);
        acknowledgment.acknowledge();
    }
    
    public static final java.util.concurrent.atomic.AtomicInteger dltCount = new java.util.concurrent.atomic.AtomicInteger(0);

    @KafkaListener(topics = KafkaConfig.TOPIC_UPDATE + ".DLT", groupId = "dlt-group")
    public void consumeDlt(PriceUpdateEvent event) {
        log.warn("Received message in DLT for product: {}", event.productId());
        dltCount.incrementAndGet();
    }
}
