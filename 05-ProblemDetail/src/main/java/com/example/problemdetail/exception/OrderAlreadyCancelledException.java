package com.example.problemdetail.exception;

public class OrderAlreadyCancelledException extends RuntimeException {
    private final Long orderId;
    public OrderAlreadyCancelledException(Long orderId) {
        super("Order already cancelled");
        this.orderId = orderId;
    }
    public Long getOrderId() { return orderId; }
}