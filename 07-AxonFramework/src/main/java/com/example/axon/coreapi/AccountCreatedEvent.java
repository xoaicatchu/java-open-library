package com.example.axon.coreapi;
import java.math.BigDecimal;
public record AccountCreatedEvent(String accountId, String owner, BigDecimal initialBalance) {}
