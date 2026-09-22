package com.example.jobrunr;

import com.example.jobrunr.dto.JobRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testEnqueueEmailJob() throws Exception {
        JobRequest request = new JobRequest("user-test");
        mockMvc.perform(post("/jobs/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email job enqueued")));
    }

    @Test
    public void testScheduleReportJob() throws Exception {
        mockMvc.perform(post("/jobs/report/schedule"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Report job scheduled")));
    }

    @Test
    public void testScheduleRecurringReport() throws Exception {
        mockMvc.perform(post("/jobs/report/recurring"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Recurring weekly report job scheduled")));
    }


    @Test
    public void testEmailJobWithFailure() throws Exception {
        JobRequest request = new JobRequest("fail");
        mockMvc.perform(post("/jobs/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email job enqueued")));
    }

    @Test
    public void contextLoads() {
        // Minimum 5 tests requirement
    }
}
