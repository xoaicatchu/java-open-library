package com.example.quartz.service;

import com.example.quartz.dto.ScheduleRequest;
import com.example.quartz.job.DataCleanupJob;
import com.example.quartz.job.ReportGenerationJob;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SchedulerService {

    private final Scheduler scheduler;

    public SchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleJob(ScheduleRequest request) throws SchedulerException {
        Class<? extends Job> jobClass = determineJobClass(request.jobClass());
        
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(request.jobName(), request.jobGroup())
                .storeDurably()
                .build();
                
        if (request.jobData() != null) {
            for (Map.Entry<String, Object> entry : request.jobData().entrySet()) {
                jobDetail.getJobDataMap().put(entry.getKey(), entry.getValue());
            }
        }

        Trigger trigger;
        if (request.cronExpression() != null && !request.cronExpression().isEmpty()) {
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(request.jobName() + "_trigger", request.jobGroup())
                    .forJob(jobDetail)
                    .withSchedule(CronScheduleBuilder.cronSchedule(request.cronExpression()))
                    .build();
        } else {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMilliseconds(request.intervalMs() != null ? request.intervalMs() : 1000);
            
            if (request.repeatCount() != null && request.repeatCount() > 0) {
                scheduleBuilder = scheduleBuilder.withRepeatCount(request.repeatCount());
            } else {
                scheduleBuilder = scheduleBuilder.repeatForever();
            }
            
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(request.jobName() + "_trigger", request.jobGroup())
                    .forJob(jobDetail)
                    .withSchedule(scheduleBuilder)
                    .build();
        }

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(new JobKey(jobName, jobGroup));
    }

    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(new JobKey(jobName, jobGroup));
    }

    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.deleteJob(new JobKey(jobName, jobGroup));
    }

    private Class<? extends Job> determineJobClass(String className) {
        if ("ReportGenerationJob".equalsIgnoreCase(className)) {
            return ReportGenerationJob.class;
        } else if ("DataCleanupJob".equalsIgnoreCase(className)) {
            return DataCleanupJob.class;
        }
        throw new IllegalArgumentException("Unknown job class: " + className);
    }
}
