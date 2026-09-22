package com.example.events.listener;

import com.example.events.entity.AuditLog;
import com.example.events.event.GenericDomainEvent;
import com.example.events.event.OrderCreatedEvent;
import com.example.events.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.context.event.EventListener;

@Component
public class AuditListener {

    private static final Logger log = LoggerFactory.getLogger(AuditListener.class);
    private final AuditLogRepository auditLogRepository;

    public AuditListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Chỉ chạy sau khi transaction commit thành công
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOrderCreatedAfterCommit(OrderCreatedEvent event) {
        log.info("AuditListener (After Commit): Logging order creation for order ID: {}", event.orderId());
        auditLogRepository.save(new AuditLog("ORDER_CREATED", event.orderId()));
    }

    // Lắng nghe sự kiện generic
    @EventListener
    public void handleGenericEvent(GenericDomainEvent<String> event) {
        log.info("AuditListener (Generic Event): Action = {}, Payload = {}", event.getAction(), event.getPayload());
    }
}
