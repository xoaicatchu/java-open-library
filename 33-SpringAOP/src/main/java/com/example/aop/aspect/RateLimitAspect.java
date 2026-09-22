package com.example.aop.aspect;

import com.example.aop.annotation.RateLimit;
import com.example.aop.exception.RateLimitExceededException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class RateLimitAspect {
    
    // In-memory store for rate limiting (methodName -> RateLimitInfo)
    // For simplicity, we limit by method name here. In reality, it should be per user/IP + method name.
    private final Map<String, RateLimitInfo> methodLimits = new ConcurrentHashMap<>();
    
    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        
        RateLimitInfo info = methodLimits.computeIfAbsent(methodName, k -> new RateLimitInfo());
        
        long now = System.currentTimeMillis();
        
        synchronized (info) {
            if (now - info.lastResetTime > rateLimit.timeWindowMs()) {
                info.counter.set(0);
                info.lastResetTime = now;
            }
            
            if (info.counter.incrementAndGet() > rateLimit.maxRequests()) {
                throw new RateLimitExceededException("Rate limit exceeded for method: " + methodName);
            }
        }
        
        return joinPoint.proceed();
    }
    
    private static class RateLimitInfo {
        AtomicInteger counter = new AtomicInteger(0);
        long lastResetTime = System.currentTimeMillis();
    }
}
