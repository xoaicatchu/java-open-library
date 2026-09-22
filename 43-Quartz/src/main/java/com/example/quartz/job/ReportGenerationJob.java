package com.example.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class ReportGenerationJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(ReportGenerationJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String reportType = context.getMergedJobDataMap().getString("reportType");
        String recipient = context.getMergedJobDataMap().getString("recipient");
        
        log.info("Generating {} report for recipient: {}", reportType != null ? reportType : "DEFAULT", recipient != null ? recipient : "NONE");
        
        // Simulating some work
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Report generation completed.");
    }
}
