package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_UPDATE = "price-updates";
    public static final String TOPIC_BATCH = "price-updates-batch";

    @Bean
    public NewTopic priceUpdatesTopic() {
        return TopicBuilder.name(TOPIC_UPDATE)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic priceUpdatesDltTopic() {
        return TopicBuilder.name(TOPIC_UPDATE + ".DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic priceUpdatesBatchTopic() {
        return TopicBuilder.name(TOPIC_BATCH)
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Configure DefaultErrorHandler with DeadLetterPublishingRecoverer
    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (r, e) -> new org.apache.kafka.common.TopicPartition(TOPIC_UPDATE + ".DLT", -1));
        // Retry 2 times with a fixed backoff of 500ms before sending to DLT
        return new DefaultErrorHandler(recoverer, new FixedBackOff(500L, 2));
    }

    // Explicit factory for batch listeners
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchFactory(
            ConsumerFactory<String, Object> consumerFactory,
            CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        // Manual ack is commonly used in batches
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
