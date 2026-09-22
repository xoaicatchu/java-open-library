package com.example.jackson.controller;

import com.example.jackson.domain.Money;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/money")
public class MoneyController {

    @GetMapping("/sample")
    public Map<String, Object> getMoneySample() {
        Money money = new Money(new BigDecimal("100.50"), "USD");
        return Map.of("price", money, "description", "Sample Product");
    }
}
