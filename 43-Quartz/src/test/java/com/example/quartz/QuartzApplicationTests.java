package com.example.quartz;

import com.example.quartz.dto.ScheduleRequest;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuartzApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Scheduler scheduler;

    @Test
    void contextLoads() {
        assertNotNull(scheduler);
    }

    @Test
    void testScheduleSimpleJob() throws Exception {
        String json = """
                {
                    "jobName": "simpleJob1",
                    "jobGroup": "group1",
                    "jobClass": "ReportGenerationJob",
                    "intervalMs": 100,
                    "repeatCount": 1,
                    "jobData": {
                        "reportType": "Daily",
                        "recipient": "admin@example.com"
                    }
                }
                """;
        mockMvc.perform(post("/api/jobs/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        JobKey jobKey = new JobKey("simpleJob1", "group1");
        assertTrue(scheduler.checkExists(jobKey));

        // Wait for execution
        await().atMost(2, TimeUnit.SECONDS).until(() -> 
            scheduler.getTriggerState(new TriggerKey("simpleJob1_trigger", "group1")) == org.quartz.Trigger.TriggerState.NONE
        );
    }

    @Test
    void testScheduleCronJob() throws Exception {
        String json = """
                {
                    "jobName": "cronJob1",
                    "jobGroup": "group2",
                    "jobClass": "DataCleanupJob",
                    "cronExpression": "0/1 * * * * ?",
                    "jobData": {
                        "daysOld": 60
                    }
                }
                """;
        mockMvc.perform(post("/api/jobs/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        JobKey jobKey = new JobKey("cronJob1", "group2");
        assertTrue(scheduler.checkExists(jobKey));
    }

    @Test
    void testPauseAndResumeJob() throws Exception {
        // Schedule job first
        String json = """
                {
                    "jobName": "pauseResumeJob",
                    "jobGroup": "group3",
                    "jobClass": "ReportGenerationJob",
                    "intervalMs": 5000,
                    "repeatCount": 10
                }
                """;
        mockMvc.perform(post("/api/jobs/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        // Pause
        mockMvc.perform(post("/api/jobs/group3/pauseResumeJob/pause"))
                .andExpect(status().isOk());
                
        assertEquals(org.quartz.Trigger.TriggerState.PAUSED, scheduler.getTriggerState(new TriggerKey("pauseResumeJob_trigger", "group3")));

        // Resume
        mockMvc.perform(post("/api/jobs/group3/pauseResumeJob/resume"))
                .andExpect(status().isOk());
                
        assertEquals(org.quartz.Trigger.TriggerState.NORMAL, scheduler.getTriggerState(new TriggerKey("pauseResumeJob_trigger", "group3")));
    }

    @Test
    void testDeleteJob() throws Exception {
        // Schedule job first
        String json = """
                {
                    "jobName": "deleteJob",
                    "jobGroup": "group4",
                    "jobClass": "DataCleanupJob",
                    "intervalMs": 5000,
                    "repeatCount": 1
                }
                """;
        mockMvc.perform(post("/api/jobs/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        JobKey jobKey = new JobKey("deleteJob", "group4");
        assertTrue(scheduler.checkExists(jobKey));

        // Delete
        mockMvc.perform(delete("/api/jobs/group4/deleteJob"))
                .andExpect(status().isOk());

        assertFalse(scheduler.checkExists(jobKey));
    }
    
    @Test
    void testListenerIsRegistered() throws Exception {
        assertNotNull(scheduler.getListenerManager().getJobListener("GlobalJobListener"));
        assertNotNull(scheduler.getListenerManager().getTriggerListener("GlobalTriggerListener"));
    }
}
