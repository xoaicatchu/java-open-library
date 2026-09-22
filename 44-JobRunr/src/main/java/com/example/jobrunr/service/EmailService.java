package com.example.jobrunr.service;

import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Job(name = "Send welcome email to %0", retries = 3)
    public void sendWelcomeEmail(String userId) {
        log.info("Sending welcome email to user: {}", userId);
        if ("fail".equals(userId)) {
            throw new RuntimeException("Simulated failure for retry");
        }
        log.info("Email sent to user: {}", userId);
    }
}
