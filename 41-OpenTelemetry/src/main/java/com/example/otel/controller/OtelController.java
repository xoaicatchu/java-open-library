package com.example.otel.controller;

import com.example.otel.dto.ProcessResponse;
import com.example.otel.service.OtelService;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otel")
public class OtelController {
    private static final Logger log = LoggerFactory.getLogger(OtelController.class);
    private final OtelService otelService;

    public OtelController(OtelService otelService) {
        this.otelService = otelService;
    }

    @GetMapping("/process")
    public ProcessResponse process(@RequestHeader(value = "user-id", required = false) String userId) {
        log.info("Received request for user: {}", userId);
        
        Baggage.current().toBuilder()
               .put("user-id", userId != null ? userId : "anonymous")
               .build()
               .storeInContext(io.opentelemetry.context.Context.current())
               .makeCurrent();

        otelService.processWithSpan();
        otelService.processManualSpan();
        
        Span currentSpan = Span.current();
        return new ProcessResponse("OK", 
                currentSpan.getSpanContext().getTraceId(), 
                currentSpan.getSpanContext().getSpanId());
    }
}
