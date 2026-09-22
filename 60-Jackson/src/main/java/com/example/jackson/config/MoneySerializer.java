package com.example.jackson.config;

import com.example.jackson.domain.Money;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@JsonComponent
public class MoneySerializer extends JsonSerializer<Money> {

    @Override
    public void serialize(Money value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        DecimalFormat format = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.US));
        String formatted = value.getCurrency() + " " + format.format(value.getAmount());
        gen.writeString(formatted);
    }
}
