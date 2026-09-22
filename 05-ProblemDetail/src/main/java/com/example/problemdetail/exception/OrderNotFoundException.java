package com.example.problemdetail.exception;

public class OrderNotFoundException extends RuntimeException {
    private final Long orderId;
    public OrderNotFoundException(Long orderId) {
        super("Order not found");
        this.orderId = orderId;
    }
    public Long getOrderId() { return orderId; }
}