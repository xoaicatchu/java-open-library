package com.example.datajpa.projection;

import java.math.BigDecimal;

public interface ProductSummary {
    String getName();
    BigDecimal getPrice();
    CategorySummary getCategory();

    interface CategorySummary {
        String getName();
    }
}
