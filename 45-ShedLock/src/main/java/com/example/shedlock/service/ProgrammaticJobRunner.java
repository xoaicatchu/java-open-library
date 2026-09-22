package com.example.shedlock.service;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class ProgrammaticJobRunner {

    private static final Logger log = LoggerFactory.getLogger(ProgrammaticJobRunner.class);
    private final LockProvider lockProvider;

    public ProgrammaticJobRunner(LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    public boolean runJobProgrammatically() {
        LockConfiguration config = new LockConfiguration(
                Instant.now(),
                "ProgrammaticJobRunner_runJob",
                Duration.ofSeconds(5),
                Duration.ofMillis(10)
        );

        Optional<SimpleLock> lock = lockProvider.lock(config);
        if (lock.isPresent()) {
            try {
                log.info("Lock acquired, running programmatic job...");
                return true;
            } finally {
                lock.get().unlock();
            }
        } else {
            log.info("Could not acquire lock, job is already running.");
            return false;
        }
    }
}
