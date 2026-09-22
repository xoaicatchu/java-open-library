package com.example.amqp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String FANOUT_EXCHANGE = "order.fanout.exchange";
    public static final String DIRECT_EXCHANGE = "payment.direct.exchange";
    public static final String TOPIC_EXCHANGE = "notification.topic.exchange";

    public static final String ORDER_EMAIL_QUEUE = "order.email.queue";
    public static final String ORDER_SMS_QUEUE = "order.sms.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    
    public static final String DLQ = "notification.dlq";
    public static final String DLX = "notification.dlx";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Dead Letter Exchange and Queue
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("dlq.routing.key");
    }

    // Exchanges
    @Bean
    public FanoutExchange orderFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public DirectExchange paymentDirectExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    // Queues with DLQ configuration
    @Bean
    public Queue orderEmailQueue() {
        return QueueBuilder.durable(ORDER_EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.routing.key")
                .build();
    }

    @Bean
    public Queue orderSmsQueue() {
        return QueueBuilder.durable(ORDER_SMS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.routing.key")
                .build();
    }

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.routing.key")
                .build();
    }

    // Bindings
    @Bean
    public Binding orderEmailBinding() {
        return BindingBuilder.bind(orderEmailQueue()).to(orderFanoutExchange());
    }

    @Bean
    public Binding orderSmsBinding() {
        return BindingBuilder.bind(orderSmsQueue()).to(orderFanoutExchange());
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue()).to(paymentDirectExchange()).with("payment.success");
    }
}
