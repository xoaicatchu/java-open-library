package com.example.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class DataCleanupJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(DataCleanupJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int daysOld = context.getMergedJobDataMap().getInt("daysOld");
        
        log.info("Starting data cleanup. Removing records older than {} days.", daysOld > 0 ? daysOld : 30);
        
        // Simulating work
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Data cleanup finished.");
    }
}
