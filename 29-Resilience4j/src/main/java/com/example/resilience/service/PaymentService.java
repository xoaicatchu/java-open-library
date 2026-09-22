package com.example.resilience.service;

import com.example.resilience.dto.PaymentRequest;
import com.example.resilience.dto.PaymentResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentService {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final AtomicInteger attempts = new AtomicInteger(0);
    private boolean simulateFailure = false;

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    public boolean isSimulateFailure() {
        return simulateFailure;
    }

    public void resetAttempts() {
        attempts.set(0);
    }
    
    public int getAttempts() {
        return attempts.get();
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayment")
    public PaymentResponse processWithCircuitBreaker(PaymentRequest request) {
        log.info("Circuit breaker call");
        if (simulateFailure) {
            throw new RuntimeException("External service failure");
        }
        return new PaymentResponse(UUID.randomUUID().toString(), "SUCCESS", "Processed");
    }

    @Retry(name = "paymentService", fallbackMethod = "fallbackPayment")
    public PaymentResponse processWithRetry(PaymentRequest request) {
        attempts.incrementAndGet();
        log.info("Retry call attempt {}", attempts.get());
        if (simulateFailure) {
            throw new RuntimeException("External service failure");
        }
        return new PaymentResponse(UUID.randomUUID().toString(), "SUCCESS", "Processed after " + attempts.get() + " attempts");
    }

    @RateLimiter(name = "paymentService", fallbackMethod = "fallbackPayment")
    public PaymentResponse processWithRateLimiter(PaymentRequest request) {
        log.info("Rate limiter call");
        return new PaymentResponse(UUID.randomUUID().toString(), "SUCCESS", "Processed");
    }

    @Bulkhead(name = "paymentService", fallbackMethod = "fallbackPayment")
    public PaymentResponse processWithBulkhead(PaymentRequest request) throws InterruptedException {
        log.info("Bulkhead call");
        Thread.sleep(500);
        return new PaymentResponse(UUID.randomUUID().toString(), "SUCCESS", "Processed");
    }

    @TimeLimiter(name = "paymentService", fallbackMethod = "fallbackFuturePayment")
    public CompletableFuture<PaymentResponse> processWithTimeLimiter(PaymentRequest request) {
        log.info("Time limiter call");
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (simulateFailure) {
                    Thread.sleep(2000); // Trigger timeout
                }
                return new PaymentResponse(UUID.randomUUID().toString(), "SUCCESS", "Processed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted", e);
            }
        });
    }

    // Fallbacks
    public PaymentResponse fallbackPayment(PaymentRequest request, Throwable t) {
        log.warn("Fallback triggered due to: {}", t.getMessage());
        return new PaymentResponse(null, "FALLBACK", "Service unavailable, fallback activated");
    }

    public CompletableFuture<PaymentResponse> fallbackFuturePayment(PaymentRequest request, Throwable t) {
        log.warn("Future Fallback triggered due to: {}", t.getMessage());
        return CompletableFuture.completedFuture(new PaymentResponse(null, "FALLBACK", "Service timeout, fallback activated"));
    }
}
