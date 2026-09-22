package com.example.problemdetail.exception;

public class PaymentDeclinedException extends RuntimeException {
    private final String reason;
    public PaymentDeclinedException(String reason) {
        super("Payment declined");
        this.reason = reason;
    }
    public String getReason() { return reason; }
}