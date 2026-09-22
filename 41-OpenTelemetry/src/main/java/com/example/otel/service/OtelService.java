package com.example.otel.service;

import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OtelService {
    private static final Logger log = LoggerFactory.getLogger(OtelService.class);
    private final Tracer tracer;
    private final LongCounter processCounter;
    private final LongHistogram processHistogram;

    public OtelService(Tracer tracer, Meter meter) {
        this.tracer = tracer;
        this.processCounter = meter.counterBuilder("process.count").setDescription("Number of processes").build();
        this.processHistogram = meter.histogramBuilder("process.duration").ofLongs().setDescription("Process duration").build();
    }

    @WithSpan("process-with-span")
    public void processWithSpan() {
        log.info("Processing using @WithSpan");
        Span.current().setAttribute("custom.attribute", "annotated");
        processCounter.add(1);
    }

    public void processManualSpan() {
        Span span = tracer.spanBuilder("manual-span").startSpan();
        long start = System.currentTimeMillis();
        try (Scope scope = span.makeCurrent()) {
            log.info("Processing using manual span");
            span.setAttribute("custom.attribute", "manual");
            processCounter.add(1);
        } finally {
            span.end();
            processHistogram.record(System.currentTimeMillis() - start);
        }
    }
}
