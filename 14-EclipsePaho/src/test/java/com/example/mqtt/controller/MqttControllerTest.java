package com.example.mqtt.controller;

import com.example.mqtt.dto.CommandRequest;
import com.example.mqtt.dto.SensorData;
import com.example.mqtt.service.MqttService;
import com.example.mqtt.service.SensorDataProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MqttController.class)
class MqttControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MqttService mqttService;

    @MockBean
    private SensorDataProcessor sensorDataProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPublishCommandSuccess() throws Exception {
        CommandRequest req = new CommandRequest("test/topic", "payload", 1, true, "key", "val");

        mockMvc.perform(post("/api/mqtt/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Published successfully"));
    }

    @Test
    void testPublishCommandFailure_NotConnected() throws Exception {
        CommandRequest req = new CommandRequest("test/topic", "payload", 1, false, null, null);
        
        doThrow(new IllegalStateException("Not connected"))
            .when(mqttService).publish(anyString(), anyString(), anyInt(), anyBoolean(), any(), any());

        mockMvc.perform(post("/api/mqtt/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("MQTT client not connected"));
    }

    @Test
    void testGetSensorData_Found() throws Exception {
        SensorData data = new SensorData("sensor-2", 15.0, 50.0, 12345L);
        when(sensorDataProcessor.getLatestReading("sensor-2")).thenReturn(data);

        mockMvc.perform(get("/api/mqtt/sensors/sensor-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("sensor-2"))
                .andExpect(jsonPath("$.temperature").value(15.0));
    }

    @Test
    void testGetSensorData_NotFound() throws Exception {
        when(sensorDataProcessor.getLatestReading("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/mqtt/sensors/unknown"))
                .andExpect(status().isNotFound());
    }
}
