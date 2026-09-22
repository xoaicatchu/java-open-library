package com.example.axon.coreapi;
import java.math.BigDecimal;
public record MoneyDepositedEvent(String accountId, BigDecimal amount) {}
