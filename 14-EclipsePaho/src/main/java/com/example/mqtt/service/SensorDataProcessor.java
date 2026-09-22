package com.example.mqtt.service;

import com.example.mqtt.dto.SensorData;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorDataProcessor {
    private final Map<String, SensorData> latestReadings = new ConcurrentHashMap<>();

    public void processData(SensorData data) {
        if (data != null && data.sensorId() != null) {
            latestReadings.put(data.sensorId(), data);
        }
    }

    public Map<String, SensorData> getAllLatestReadings() {
        return Map.copyOf(latestReadings);
    }

    public SensorData getLatestReading(String sensorId) {
        return latestReadings.get(sensorId);
    }
}
