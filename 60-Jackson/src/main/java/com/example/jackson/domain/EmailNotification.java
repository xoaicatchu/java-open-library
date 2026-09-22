package com.example.jackson.domain;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.UUID;

public class EmailNotification extends Notification {

    @JsonView(Views.DetailView.class)
    private String emailAddress;

    public EmailNotification() {}

    public EmailNotification(UUID id, String message, String emailAddress) {
        super(id, message);
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
