package com.example.mqtt.dto;

public record SensorData(
    String sensorId,
    double temperature,
    double humidity,
    long timestamp
) {
}
