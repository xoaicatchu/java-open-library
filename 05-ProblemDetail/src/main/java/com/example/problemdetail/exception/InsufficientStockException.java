package com.example.problemdetail.exception;

public class InsufficientStockException extends RuntimeException {
    private final String productId;
    public InsufficientStockException(String productId) {
        super("Insufficient stock");
        this.productId = productId;
    }
    public String getProductId() { return productId; }
}