package com.example.mapstruct.mapper;

import com.example.mapstruct.dto.*;
import com.example.mapstruct.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MapStructTests {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void testBasicMappingEntityToDto() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        CustomerInfo dto = customerMapper.toDto(customer);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("John Doe");
        assertThat(dto.email()).isEqualTo("john@example.com");
    }

    @Test
    void testBasicMappingDtoToEntity() {
        CustomerInfo dto = new CustomerInfo(2L, "Jane Doe", "jane@example.com");
        Customer customer = customerMapper.toEntity(dto);
        assertThat(customer.getId()).isEqualTo(2L);
        assertThat(customer.getName()).isEqualTo("Jane Doe");
        assertThat(customer.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void testUpdateMapping() {
        Customer customer = new Customer();
        customer.setId(5L);
        customer.setName("Old Name");
        customer.setEmail("old@example.com");

        CustomerInfo updateDto = new CustomerInfo(99L, "New Name", "new@example.com");
        customerMapper.updateCustomerFromDto(updateDto, customer);

        // ID should be ignored as per @Mapping(target = "id", ignore = true)
        assertThat(customer.getId()).isEqualTo(5L);
        assertThat(customer.getName()).isEqualTo("New Name");
        assertThat(customer.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testNestedMapping() {
        Category category = new Category();
        category.setId(10L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setCategory(category);
        product.setStatus(1);

        ProductSummary dto = productMapper.toDto(product);
        assertThat(dto.name()).isEqualTo("Laptop");
        assertThat(dto.category()).isNotNull();
        assertThat(dto.category().id()).isEqualTo(10L);
        assertThat(dto.category().name()).isEqualTo("Electronics");
    }

    @Test
    void testCustomQualifier() {
        Product product = new Product();
        product.setStatus(0); // 0 maps to INACTIVE

        ProductSummary dto = productMapper.toDto(product);
        assertThat(dto.statusString()).isEqualTo("INACTIVE");
        
        product.setStatus(1); // 1 maps to ACTIVE
        ProductSummary dto2 = productMapper.toDto(product);
        assertThat(dto2.statusString()).isEqualTo("ACTIVE");
    }

    @Test
    void testExpressionMapping() {
        Product product = new Product();
        product.setPrice(new BigDecimal("150.00"));

        ProductSummary dto = productMapper.toDto(product);
        assertThat(dto.formattedPrice()).isEqualTo("$150.00");
    }

    @Test
    void testCollectionMapping() {
        Product p1 = new Product();
        p1.setName("Product 1");
        p1.setPrice(new BigDecimal("10"));

        Product p2 = new Product();
        p2.setName("Product 2");
        p2.setPrice(new BigDecimal("20"));

        List<ProductSummary> dtos = productMapper.toDtoList(List.of(p1, p2));
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("Product 1");
        assertThat(dtos.get(1).name()).isEqualTo("Product 2");
    }

    @Test
    void testMultipleSourceMapping() {
        Order order = new Order();
        order.setOrderNumber("ORD-12345");

        Customer customer = new Customer();
        customer.setName("Alice");
        customer.setEmail("alice@example.com");

        OrderResponse response = orderMapper.toOrderResponse(order, customer);
        assertThat(response.orderNum()).isEqualTo("ORD-12345");
        assertThat(response.customerEmail()).isEqualTo("alice@example.com");
        assertThat(response.customerName()).isEqualTo("Alice");
    }
}
