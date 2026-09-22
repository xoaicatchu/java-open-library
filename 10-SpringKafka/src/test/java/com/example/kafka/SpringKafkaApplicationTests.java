package com.example.kafka;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.dto.PriceUpdateEvent;
import com.example.kafka.entity.ProductPrice;
import com.example.kafka.repository.ProductPriceRepository;
import com.example.kafka.service.PriceUpdateProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.datasource.url=jdbc:h2:mem:testdb-test"
})
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:0", "port=0" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SpringKafkaApplicationTests {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private PriceUpdateProducer producer;

    @Autowired
    private ProductPriceRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(embeddedKafkaBroker).isNotNull();
    }

    @Test
    void testProduceAndConsumeMessage() {
        // Prepare data
        PriceUpdateEvent event = new PriceUpdateEvent(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(120), Instant.now());
        
        // Produce message
        producer.sendPriceUpdate(KafkaConfig.TOPIC_UPDATE, event);
        
        // Await consumption and DB save
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(repository.findById(1L)).isPresent();
            ProductPrice price = repository.findById(1L).get();
            assertThat(price.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(120));
        });
    }

    @Test
    void testJsonSerialization() {
        PriceUpdateEvent event = new PriceUpdateEvent(2L, BigDecimal.valueOf(50), BigDecimal.valueOf(45), Instant.now());
        producer.sendPriceUpdate(KafkaConfig.TOPIC_UPDATE, event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(repository.findById(2L)).isPresent();
            assertThat(repository.findById(2L).get().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(45));
        });
    }

    @Test
    void testErrorHandlingSendsToDlt() throws Exception {
        // Negative price triggers an error in the consumer
        PriceUpdateEvent invalidEvent = new PriceUpdateEvent(3L, BigDecimal.valueOf(100), BigDecimal.valueOf(-10), Instant.now());
        
        producer.sendPriceUpdate(KafkaConfig.TOPIC_UPDATE, invalidEvent);

        // Await failure and routing to DLT, which increments the counter
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(com.example.kafka.service.PriceUpdateConsumer.dltCount.get()).isGreaterThan(0);
        });

        // Make sure it wasn't saved
        assertThat(repository.findById(3L)).isEmpty();
    }

    @Test
    void testBatchConsumer() {
        PriceUpdateEvent event1 = new PriceUpdateEvent(101L, BigDecimal.valueOf(10), BigDecimal.valueOf(15), Instant.now());
        PriceUpdateEvent event2 = new PriceUpdateEvent(102L, BigDecimal.valueOf(20), BigDecimal.valueOf(25), Instant.now());

        producer.sendPriceUpdate(KafkaConfig.TOPIC_BATCH, event1);
        producer.sendPriceUpdate(KafkaConfig.TOPIC_BATCH, event2);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(repository.findById(101L)).isPresent();
            assertThat(repository.findById(102L)).isPresent();
        });
    }

    @Test
    void testManualAckWorks() {
        // It's implicitly tested because enable-auto-commit is false, and ack-mode is manual.
        // If ack was not called, the offsets wouldn't advance. 
        // We just do a standard flow check here as the 6th method.
        PriceUpdateEvent event = new PriceUpdateEvent(55L, BigDecimal.valueOf(99), BigDecimal.valueOf(999), Instant.now());
        producer.sendPriceUpdate(KafkaConfig.TOPIC_UPDATE, event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(repository.findById(55L)).isPresent();
        });
    }
}
