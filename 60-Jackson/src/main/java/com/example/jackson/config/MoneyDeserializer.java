package com.example.jackson.config;

import com.example.jackson.domain.Money;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;

@JsonComponent
public class MoneyDeserializer extends JsonDeserializer<Money> {
    
    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        String[] parts = text.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid money format");
        }
        String currency = parts[0];
        String amountStr = parts[1].replace(",", "");
        return new Money(new BigDecimal(amountStr), currency);
    }
}
