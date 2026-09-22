package com.example.jackson.domain;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.UUID;

public class PushNotification extends Notification {

    @JsonView(Views.DetailView.class)
    private String deviceToken;

    public PushNotification() {}

    public PushNotification(UUID id, String message, String deviceToken) {
        super(id, message);
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
