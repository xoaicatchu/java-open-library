package com.example.resilience.controller;

import com.example.resilience.dto.PaymentRequest;
import com.example.resilience.dto.PaymentResponse;
import com.example.resilience.service.PaymentService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public PaymentController(PaymentService paymentService, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.paymentService = paymentService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostMapping("/circuit-breaker")
    public PaymentResponse circuitBreaker(@RequestBody PaymentRequest request) {
        return paymentService.processWithCircuitBreaker(request);
    }

    @PostMapping("/retry")
    public PaymentResponse retry(@RequestBody PaymentRequest request) {
        return paymentService.processWithRetry(request);
    }

    @PostMapping("/rate-limiter")
    public PaymentResponse rateLimiter(@RequestBody PaymentRequest request) {
        return paymentService.processWithRateLimiter(request);
    }

    @PostMapping("/bulkhead")
    public PaymentResponse bulkhead(@RequestBody PaymentRequest request) throws InterruptedException {
        return paymentService.processWithBulkhead(request);
    }

    @PostMapping("/time-limiter")
    public CompletableFuture<PaymentResponse> timeLimiter(@RequestBody PaymentRequest request) {
        return paymentService.processWithTimeLimiter(request);
    }

    @PostMapping("/programmatic")
    public PaymentResponse programmatic(@RequestBody PaymentRequest request) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("paymentService");
        Supplier<PaymentResponse> supplier = () -> {
            if (paymentService.isSimulateFailure()) {
                throw new RuntimeException("External service failure");
            }
            return new PaymentResponse("prog-1", "SUCCESS", "Processed");
        };
        Supplier<PaymentResponse> decoratedSupplier = CircuitBreaker.decorateSupplier(cb, supplier);
        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return new PaymentResponse(null, "PROGRAMMATIC_FALLBACK", "Handled Programmatically");
        }
    }
    
    @PostMapping("/toggle-failure")
    public void toggleFailure(@RequestParam boolean fail) {
        paymentService.setSimulateFailure(fail);
    }
    
    @PostMapping("/reset-attempts")
    public void resetAttempts() {
        paymentService.resetAttempts();
    }
    
    @GetMapping("/attempts")
    public int getAttempts() {
        return paymentService.getAttempts();
    }
}
