package com.example.amqp;

import com.example.amqp.config.RabbitConfig;
import com.example.amqp.dto.OrderNotification;
import com.example.amqp.dto.PaymentNotification;
import com.example.amqp.service.NotificationConsumer;
import com.example.amqp.service.NotificationProducer;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class AmqpApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitConfig rabbitConfig;
    
    @Autowired
    private MessageConverter jsonMessageConverter;
    
    @MockBean
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private NotificationProducer notificationProducer;

    @Test
    void testMessageConverterBeanExists() {
        assertNotNull(jsonMessageConverter);
        assertEquals("org.springframework.amqp.support.converter.Jackson2JsonMessageConverter", jsonMessageConverter.getClass().getName());
    }

    @Test
    void testExchangeDeclarationsExist() {
        Exchange fanout = rabbitConfig.orderFanoutExchange();
        assertNotNull(fanout);
        assertEquals(RabbitConfig.FANOUT_EXCHANGE, fanout.getName());
        assertEquals("fanout", fanout.getType());
        
        Exchange direct = rabbitConfig.paymentDirectExchange();
        assertNotNull(direct);
        assertEquals(RabbitConfig.DIRECT_EXCHANGE, direct.getName());
        assertEquals("direct", direct.getType());
    }

    @Test
    void testQueueDeclarationsExistAndHaveDlqArgs() {
        Queue emailQueue = rabbitConfig.orderEmailQueue();
        assertNotNull(emailQueue);
        assertEquals(RabbitConfig.ORDER_EMAIL_QUEUE, emailQueue.getName());
        assertTrue(emailQueue.getArguments().containsKey("x-dead-letter-exchange"));
        assertEquals(RabbitConfig.DLX, emailQueue.getArguments().get("x-dead-letter-exchange"));
    }

    @Test
    void testProducerSendsOrderNotificationToFanout() {
        OrderNotification notification = new OrderNotification("O123", "C456", new BigDecimal("100.00"));
        
        notificationProducer.sendOrderNotification(notification);
        
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitConfig.FANOUT_EXCHANGE),
            eq(""),
            eq(notification)
        );
    }
    
    @Test
    void testProducerSendsPaymentNotificationAndExpectsReply() {
        PaymentNotification notification = new PaymentNotification("P789", "O123", new BigDecimal("100.00"), "SUCCESS");
        
        when(rabbitTemplate.convertSendAndReceive(
                eq(RabbitConfig.DIRECT_EXCHANGE),
                eq("payment.success"),
                eq(notification)
        )).thenReturn("Processed payment P789");
        
        String reply = notificationProducer.sendAndReceive(notification);
        
        assertEquals("Processed payment P789", reply);
        
        verify(rabbitTemplate, times(1)).convertSendAndReceive(
            eq(RabbitConfig.DIRECT_EXCHANGE),
            eq("payment.success"),
            eq(notification)
        );
    }
}
