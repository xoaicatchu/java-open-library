package com.example.datafaker.faker;

import net.datafaker.Faker;
import java.util.Locale;
import java.util.Random;

public class MyFaker extends Faker {
    
    public MyFaker() {
        super();
    }
    
    public MyFaker(Locale locale) {
        super(locale);
    }

    public MyFaker(Random random) {
        super(random);
    }

    public ECommerceProvider eCommerce() {
        return getProvider(ECommerceProvider.class, ECommerceProvider::new, this);
    }
}
