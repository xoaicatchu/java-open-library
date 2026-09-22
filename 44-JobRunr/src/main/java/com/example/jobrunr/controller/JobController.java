package com.example.jobrunr.controller;

import com.example.jobrunr.dto.JobRequest;
import com.example.jobrunr.service.EmailService;
import com.example.jobrunr.service.ReportService;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobScheduler jobScheduler;
    private final EmailService emailService;
    private final ReportService reportService;

    public JobController(JobScheduler jobScheduler, EmailService emailService, ReportService reportService) {
        this.jobScheduler = jobScheduler;
        this.emailService = emailService;
        this.reportService = reportService;
    }

    @PostMapping("/email")
    public ResponseEntity<String> enqueueEmailJob(@RequestBody JobRequest request) {
        // Fire-and-forget job
        jobScheduler.enqueue(() -> emailService.sendWelcomeEmail(request.userId()));
        return ResponseEntity.ok("Email job enqueued for user: " + request.userId());
    }

    @PostMapping("/report/schedule")
    public ResponseEntity<String> scheduleReportJob() {
        // Scheduled job (in 1 minute)
        Instant inOneMinute = Instant.now().plus(1, ChronoUnit.MINUTES);
        jobScheduler.schedule(inOneMinute, () -> reportService.generateDailyReport(LocalDate.now()));
        return ResponseEntity.ok("Report job scheduled for 1 minute from now");
    }

    @PostMapping("/report/recurring")
    public ResponseEntity<String> scheduleRecurringReport() {
        // Recurring job
        jobScheduler.scheduleRecurrently("weekly-report", Cron.weekly(), () -> reportService.generateWeeklyReport());
        return ResponseEntity.ok("Recurring weekly report job scheduled");
    }
    
}
