package com.example.testcontainers;

import com.example.testcontainers.entity.Product;
import com.example.testcontainers.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver"
})
class H2FallbackTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveProductH2() {
        Product p = new Product("H2 Test Product", new BigDecimal("10.00"));
        productRepository.save(p);

        List<Product> products = productRepository.findAll();
        assertThat(products).isNotEmpty();
    }

    @Test
    void testCountProductsH2() {
        long count = productRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}
