package com.example.shedlock.controller;

import com.example.shedlock.scheduler.CleanupScheduler;
import com.example.shedlock.scheduler.ReportScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final ReportScheduler reportScheduler;
    private final CleanupScheduler cleanupScheduler;
    private final JdbcTemplate jdbcTemplate;

    public JobController(ReportScheduler reportScheduler,
                         CleanupScheduler cleanupScheduler,
                         JdbcTemplate jdbcTemplate) {
        this.reportScheduler = reportScheduler;
        this.cleanupScheduler = cleanupScheduler;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/report")
    public ResponseEntity<String> triggerReport() {
        reportScheduler.generateReport();
        return ResponseEntity.ok("Report generation triggered");
    }

    @PostMapping("/cleanup")
    public ResponseEntity<String> triggerCleanup() {
        cleanupScheduler.cleanupOldData();
        return ResponseEntity.ok("Cleanup triggered");
    }

    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getJobStatus() {
        List<Map<String, Object>> locks = jdbcTemplate.queryForList("SELECT * FROM shedlock");
        return ResponseEntity.ok(locks);
    }
}
