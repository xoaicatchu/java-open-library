package com.example.junit5.service;

public interface PaymentGateway {
    boolean processPayment(Double amount);
}
