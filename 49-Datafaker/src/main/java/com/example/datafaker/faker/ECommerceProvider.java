package com.example.datafaker.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class ECommerceProvider extends AbstractProvider<BaseProviders> {

    public ECommerceProvider(BaseProviders faker) {
        super(faker);
    }

    public String orderStatus() {
        return faker.options().option("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
    }
    
    public String discountCode() {
        return "DISCOUNT" + faker.number().numberBetween(10, 50);
    }
}
