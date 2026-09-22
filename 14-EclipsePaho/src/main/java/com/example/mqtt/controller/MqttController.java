package com.example.mqtt.controller;

import com.example.mqtt.dto.CommandRequest;
import com.example.mqtt.dto.SensorData;
import com.example.mqtt.service.MqttService;
import com.example.mqtt.service.SensorDataProcessor;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    private final MqttService mqttService;
    private final SensorDataProcessor sensorDataProcessor;

    public MqttController(MqttService mqttService, SensorDataProcessor sensorDataProcessor) {
        this.mqttService = mqttService;
        this.sensorDataProcessor = sensorDataProcessor;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishCommand(@RequestBody CommandRequest request) {
        try {
            mqttService.publish(
                request.topic(),
                request.payload(),
                request.qos(),
                request.retained(),
                request.userPropertyKey(),
                request.userPropertyValue()
            );
            return ResponseEntity.ok(Map.of("message", "Published successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().body(
                ProblemDetail.forStatusAndDetail(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "MQTT client not connected")
            );
        } catch (MqttException e) {
            return ResponseEntity.internalServerError().body(
                ProblemDetail.forStatusAndDetail(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "MQTT error: " + e.getMessage())
            );
        }
    }

    @GetMapping("/sensors")
    public Map<String, SensorData> getAllReadings() {
        return sensorDataProcessor.getAllLatestReadings();
    }

    @GetMapping("/sensors/{sensorId}")
    public ResponseEntity<SensorData> getReading(@PathVariable String sensorId) {
        SensorData data = sensorDataProcessor.getLatestReading(sensorId);
        if (data != null) {
            return ResponseEntity.ok(data);
        }
        return ResponseEntity.notFound().build();
    }
}
