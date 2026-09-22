package com.example.shedlock.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(CleanupScheduler.class);
    private final AtomicInteger executionCount = new AtomicInteger(0);

    @Scheduled(fixedDelay = 2000)
    @SchedulerLock(name = "CleanupScheduler_cleanupOldData", lockAtLeastFor = "1s", lockAtMostFor = "3s")
    public void cleanupOldData() {
        log.info("Cleaning up old data... Thread: {}", Thread.currentThread().getName());
        executionCount.incrementAndGet();
    }

    public int getExecutionCount() {
        return executionCount.get();
    }
}
