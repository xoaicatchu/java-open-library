package com.example.jackson.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.UUID;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmailNotification.class, name = "EMAIL"),
    @JsonSubTypes.Type(value = SmsNotification.class, name = "SMS"),
    @JsonSubTypes.Type(value = PushNotification.class, name = "PUSH")
})
public abstract class Notification {
    
    @JsonView(Views.Public.class)
    private UUID id;
    
    @JsonView(Views.Public.class)
    private String message;

    public Notification() {}

    public Notification(UUID id, String message) {
        this.id = id;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
