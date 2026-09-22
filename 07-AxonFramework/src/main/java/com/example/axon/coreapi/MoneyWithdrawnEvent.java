package com.example.axon.coreapi;
import java.math.BigDecimal;
public record MoneyWithdrawnEvent(String accountId, BigDecimal amount) {}
