package com.example.shedlock.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);
    private final AtomicInteger executionCount = new AtomicInteger(0);

    @Scheduled(fixedRate = 1000)
    @SchedulerLock(name = "ReportScheduler_generateReport", lockAtLeastFor = "500ms", lockAtMostFor = "2s")
    public void generateReport() {
        log.info("Generating report... Thread: {}", Thread.currentThread().getName());
        executionCount.incrementAndGet();
    }

    public int getExecutionCount() {
        return executionCount.get();
    }
}
