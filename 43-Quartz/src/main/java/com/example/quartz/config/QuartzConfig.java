package com.example.quartz.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.EverythingMatcher;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class QuartzConfig {

    private final Scheduler scheduler;
    private final GlobalJobListener jobListener;
    private final GlobalTriggerListener triggerListener;

    public QuartzConfig(Scheduler scheduler, GlobalJobListener jobListener, GlobalTriggerListener triggerListener) {
        this.scheduler = scheduler;
        this.jobListener = jobListener;
        this.triggerListener = triggerListener;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.getListenerManager().addJobListener(jobListener, EverythingMatcher.allJobs());
        scheduler.getListenerManager().addTriggerListener(triggerListener, EverythingMatcher.allTriggers());
    }
}
