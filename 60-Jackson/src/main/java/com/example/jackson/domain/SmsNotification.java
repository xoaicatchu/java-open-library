package com.example.jackson.domain;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.UUID;

public class SmsNotification extends Notification {

    @JsonView(Views.DetailView.class)
    private String phoneNumber;

    public SmsNotification() {}

    public SmsNotification(UUID id, String message, String phoneNumber) {
        super(id, message);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
