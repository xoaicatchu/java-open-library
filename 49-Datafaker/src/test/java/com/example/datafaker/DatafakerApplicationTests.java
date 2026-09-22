package com.example.datafaker;

import com.example.datafaker.entity.Customer;
import com.example.datafaker.entity.Product;
import com.example.datafaker.faker.MyFaker;
import com.example.datafaker.service.FakerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatafakerApplicationTests {

    @Autowired
    private FakerService fakerService;

    @Test
    void testVietnameseLocale() {
        List<Customer> vnCustomers = fakerService.generateVietnameseCustomers(2);
        assertThat(vnCustomers).hasSize(2);
        assertThat(vnCustomers.get(0).getFullName()).isNotNull();
    }

    @Test
    void testDeterministicSeed() {
        Customer c1 = fakerService.generateDeterministicCustomer(12345L);
        Customer c2 = fakerService.generateDeterministicCustomer(12345L);
        assertThat(c1.getFullName()).isEqualTo(c2.getFullName());
        assertThat(c1.getEmail()).isEqualTo(c2.getEmail());
    }

    @Test
    void testCustomProvider() {
        MyFaker faker = new MyFaker();
        String orderStatus = faker.eCommerce().orderStatus();
        assertThat(orderStatus).isIn("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        
        String discountCode = fakerService.generateDiscountCode();
        assertThat(discountCode).startsWith("DISCOUNT");
    }

    @Test
    void testExpressionBasedGeneration() {
        List<Product> products = fakerService.generateProductsFromExpression(3);
        assertThat(products).hasSize(3);
        assertThat(products.get(0).getName()).isNotNull();
        assertThat(products.get(0).getDepartment()).isNotNull();
    }

    @Test
    void testCollectionGeneration() {
        MyFaker faker = new MyFaker();
        List<String> words = faker.collection(() -> faker.lorem().word()).len(5).generate();
        assertThat(words).hasSize(5);
    }

    @Test
    void testDatabaseSeeding() {
        fakerService.seedDatabase();
        List<Customer> allCustomers = fakerService.getAllCustomers();
        assertThat(allCustomers).hasSizeGreaterThanOrEqualTo(5);
    }
    
    @Test
    void testBasicProviders() {
        MyFaker faker = new MyFaker();
        assertThat(faker.name().fullName()).isNotBlank();
        assertThat(faker.address().fullAddress()).isNotBlank();
        assertThat(faker.internet().emailAddress()).isNotBlank();
        assertThat(faker.phoneNumber().phoneNumber()).isNotBlank();
        assertThat(faker.commerce().productName()).isNotBlank();
    }
}
