package com.example.assertj;

import com.example.assertj.asserts.OrderAssert;
import com.example.assertj.domain.Customer;
import com.example.assertj.domain.Order;
import com.example.assertj.domain.Product;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class AssertJTests {

    private Order order;
    private Customer customer;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        customer = new Customer("c1", "John Doe", "john@example.com");
        products = List.of(
                new Product("p1", "Laptop", 1200.0),
                new Product("p2", "Mouse", 25.5)
        );
        order = new Order(
                "o1",
                customer,
                products,
                Map.of("source", "web", "priority", "high"),
                LocalDateTime.now().minusHours(2)
        );
    }

    @Test
    void basicAssertions() {
        assertThat(order).isNotNull();
        assertThat(order.getOrderId()).isEqualTo("o1");
        assertThat(order).isInstanceOf(Order.class);
    }

    @Test
    void stringAssertions() {
        assertThat(customer.getName())
                .startsWith("John")
                .contains(" ")
                .endsWith("Doe")
                .matches("^[A-Z][a-z]+\\s[A-Z][a-z]+$");
    }

    @Test
    void collectionAssertions() {
        assertThat(order.getItems())
                .hasSize(2)
                .contains(products.get(0), products.get(1))
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Mouse", "Laptop");

        assertThat(order.getItems())
                .filteredOn(p -> p.getPrice() > 100)
                .hasSize(1)
                .extracting("name")
                .containsOnly("Laptop");
    }

    @Test
    void exceptionAssertions() {
        Order invalidOrder = new Order("o2", customer, new ArrayList<>(), Map.of(), LocalDateTime.now());
        
        assertThatThrownBy(invalidOrder::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");

        assertThatCode(order::validate).doesNotThrowAnyException();
    }

    @Test
    void recursiveComparisonAssertions() {
        Order expectedOrder = new Order(
                "o1",
                new Customer("c1", "John Doe", "john@example.com"),
                List.of(new Product("p1", "Laptop", 1200.0), new Product("p2", "Mouse", 25.5)),
                Map.of("source", "web", "priority", "high"),
                LocalDateTime.now() // Different time
        );

        assertThat(order).usingRecursiveComparison()
                .ignoringFields("orderDate")
                .isEqualTo(expectedOrder);
    }

    @Test
    void softAssertions() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(customer.getName()).isEqualTo("John Doe");
        softly.assertThat(customer.getEmail()).contains("@");
        softly.assertThat(order.getItems()).hasSize(2);
        softly.assertAll();
    }

    @Test
    void customAssertions() {
        OrderAssert.assertThat(order)
                .hasOrderId("o1")
                .hasItemsCount(2);
    }

    @Test
    void mapAssertions() {
        assertThat(order.getMetadata())
                .isNotEmpty()
                .hasSize(2)
                .containsKey("source")
                .containsEntry("priority", "high")
                .doesNotContainKey("discount");
    }

    @Test
    void dateTimeAssertions() {
        LocalDateTime now = LocalDateTime.now();
        assertThat(order.getOrderDate())
                .isBefore(now)
                .isAfter(now.minusHours(5))
                .isCloseTo(now.minusHours(2), within(1, java.time.temporal.ChronoUnit.MINUTES));
    }
    
    @Test
    void objectConditionAssertions() {
        org.assertj.core.api.Condition<Product> expensive = new org.assertj.core.api.Condition<>(
                p -> p.getPrice() > 1000, "expensive product"
        );
        
        assertThat(order.getItems())
                .haveExactly(1, expensive);
    }
}
