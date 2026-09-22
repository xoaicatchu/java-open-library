package com.example.mqtt.service;

import com.example.mqtt.dto.SensorData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensorDataProcessorTest {

    private SensorDataProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SensorDataProcessor();
    }

    @Test
    void testProcessAndRetrieveData() {
        SensorData data = new SensorData("sensor-1", 22.5, 45.0, System.currentTimeMillis());
        processor.processData(data);

        SensorData retrieved = processor.getLatestReading("sensor-1");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.temperature()).isEqualTo(22.5);
    }

    @Test
    void testProcessUpdatesExistingData() {
        SensorData data1 = new SensorData("sensor-1", 22.5, 45.0, 1000L);
        processor.processData(data1);

        SensorData data2 = new SensorData("sensor-1", 23.0, 40.0, 2000L);
        processor.processData(data2);

        SensorData retrieved = processor.getLatestReading("sensor-1");
        assertThat(retrieved.temperature()).isEqualTo(23.0);
        assertThat(processor.getAllLatestReadings()).hasSize(1);
    }
}
