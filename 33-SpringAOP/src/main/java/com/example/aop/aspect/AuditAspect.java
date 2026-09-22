package com.example.aop.aspect;

import com.example.aop.annotation.Auditable;
import com.example.aop.entity.AuditLog;
import com.example.aop.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuditActivity(JoinPoint joinPoint, Auditable auditable, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String action = auditable.action();
        
        String details = String.format("Method %s executed successfully.", methodName);
        
        AuditLog log = new AuditLog(action, details, LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
