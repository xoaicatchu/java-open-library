package com.example.redisson.dto;

import java.io.Serializable;

public record CartItem(String productId, int quantity, double price) implements Serializable {}
