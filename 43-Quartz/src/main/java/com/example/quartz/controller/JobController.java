package com.example.quartz.controller;

import com.example.quartz.dto.ScheduleRequest;
import com.example.quartz.service.SchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.quartz.SchedulerException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final SchedulerService schedulerService;

    public JobController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<Void> schedule(@RequestBody ScheduleRequest request) throws SchedulerException {
        schedulerService.scheduleJob(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{group}/{name}/pause")
    public ResponseEntity<Void> pause(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.pauseJob(name, group);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{group}/{name}/resume")
    public ResponseEntity<Void> resume(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.resumeJob(name, group);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{group}/{name}")
    public ResponseEntity<Void> delete(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.deleteJob(name, group);
        return ResponseEntity.ok().build();
    }
    
    @ExceptionHandler(SchedulerException.class)
    public ProblemDetail handleSchedulerException(SchedulerException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
