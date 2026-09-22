package com.example.restclient.exception;

import org.springframework.http.HttpStatus;

public class CustomClientException extends RuntimeException {
    private final HttpStatus status;

    public CustomClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
