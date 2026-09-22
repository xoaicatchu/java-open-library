package com.example.otel.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WithSpanAspect {
    private final Tracer tracer;

    public WithSpanAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("@annotation(withSpan)")
    public Object traceMethod(ProceedingJoinPoint pjp, WithSpan withSpan) throws Throwable {
        String spanName = withSpan.value().isEmpty() ? pjp.getSignature().getName() : withSpan.value();
        Span span = tracer.spanBuilder(spanName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return pjp.proceed();
        } catch (Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}
