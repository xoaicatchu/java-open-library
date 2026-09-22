package com.example.mqtt.dto;

public record CommandRequest(
    String topic,
    String payload,
    int qos,
    boolean retained,
    String userPropertyKey,
    String userPropertyValue
) {
}
