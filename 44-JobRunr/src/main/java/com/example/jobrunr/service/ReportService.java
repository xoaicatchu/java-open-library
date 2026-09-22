package com.example.jobrunr.service;

import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Job(name = "Generate daily report for %0")
    public void generateDailyReport(LocalDate date) {
        log.info("Generating daily report for date: {}", date);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Report generated for date: {}", date);
    }
    
    @Job(name = "Generate weekly report")
    public void generateWeeklyReport() {
        log.info("Generating weekly report");
    }
}
