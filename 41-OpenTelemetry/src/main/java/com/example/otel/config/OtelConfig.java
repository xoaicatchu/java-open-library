package com.example.otel.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfig {
    
    @Bean
    public Tracer customTracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.otel.tracer");
    }

    @Bean
    public Meter customMeter(OpenTelemetry openTelemetry) {
        return openTelemetry.getMeter("com.example.otel.meter");
    }
}
