package com.example.quartz.dto;

import java.util.Map;

public record ScheduleRequest(
        String jobName,
        String jobGroup,
        String jobClass, // e.g. "ReportGenerationJob"
        String cronExpression,
        Integer repeatCount, // for simple trigger
        Long intervalMs, // for simple trigger
        Map<String, Object> jobData
) {
}
