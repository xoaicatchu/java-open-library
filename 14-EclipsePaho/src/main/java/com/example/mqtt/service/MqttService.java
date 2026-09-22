package com.example.mqtt.service;

import com.example.mqtt.dto.SensorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;

@Service
public class MqttService {
    private static final Logger log = LoggerFactory.getLogger(MqttService.class);

    private MqttClient client;
    private final SensorDataProcessor sensorDataProcessor;
    private final ObjectMapper objectMapper;

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.auto-reconnect}")
    private boolean autoReconnect;

    public MqttService(SensorDataProcessor sensorDataProcessor, ObjectMapper objectMapper) {
        this.sensorDataProcessor = sensorDataProcessor;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setAutomaticReconnect(autoReconnect);
            options.setCleanStart(true);
            options.setConnectionTimeout(10);

            client.setCallback(new MqttCallback() {
                @Override
                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    log.warn("Disconnected from MQTT Broker: {}", disconnectResponse.getException().getMessage());
                }

                @Override
                public void mqttErrorOccurred(MqttException exception) {
                    log.error("MQTT Error: {}", exception.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    log.info("Message arrived on topic: {}, QoS: {}, payload: {}", topic, message.getQos(), new String(message.getPayload()));
                    if (topic.startsWith("sensors/")) {
                        SensorData data = objectMapper.readValue(message.getPayload(), SensorData.class);
                        sensorDataProcessor.processData(data);
                    }
                }

                @Override
                public void deliveryComplete(IMqttToken token) {
                    log.debug("Delivery complete: {}", token.getMessageId());
                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    log.info("Connected to MQTT Broker: {}, reconnected: {}", serverURI, reconnect);
                    try {
                        subscribe("sensors/+/temperature", 1);
                    } catch (MqttException e) {
                        log.error("Failed to subscribe after connection", e);
                    }
                }
                
                @Override
                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                }
            });

            // Connect to broker. We catch exception if broker is down so app still starts up for testing.
            try {
                client.connect(options);
            } catch (MqttException e) {
                log.warn("Failed to connect to MQTT broker on startup. It might be offline.", e);
            }

        } catch (MqttException e) {
            log.error("Failed to initialize MQTT Client", e);
        }
    }

    public void publish(String topic, String payload, int qos, boolean retained, String userPropKey, String userPropValue) throws MqttException {
        if (client == null || !client.isConnected()) {
            throw new IllegalStateException("MQTT Client is not connected");
        }
        
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        message.setRetained(retained);
        
        if (userPropKey != null && userPropValue != null) {
            MqttProperties properties = new MqttProperties();
            List<UserProperty> userProperties = new ArrayList<>();
            userProperties.add(new UserProperty(userPropKey, userPropValue));
            properties.setUserProperties(userProperties);
            message.setProperties(properties);
        }
        
        client.publish(topic, message);
        log.info("Published message to topic {} with QoS {}", topic, qos);
    }

    public void subscribe(String topic, int qos) throws MqttException {
        if (client != null && client.isConnected()) {
            client.subscribe(topic, qos);
            log.info("Subscribed to topic {} with QoS {}", topic, qos);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
            }
        } catch (MqttException e) {
            log.error("Error disconnecting MQTT client", e);
        }
    }
}
