package com.example.restclient;

import com.example.restclient.config.RestClientConfig;
import com.example.restclient.dto.ProductRecord;
import com.example.restclient.exception.CustomClientException;
import com.example.restclient.service.ProductApiClient;
import com.example.restclient.service.ProductHttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest({ProductApiClient.class})
@Import(RestClientConfig.class)
class RestClientApplicationTests {

    @Autowired
    private ProductApiClient productApiClient;

    @Autowired
    private ProductHttpExchange productHttpExchange;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllProducts() throws Exception {
        List<ProductRecord> mockProducts = List.of(
                new ProductRecord(1L, "Laptop", 1200.0),
                new ProductRecord(2L, "Phone", 800.0)
        );

        mockServer.expect(requestTo("http://localhost:8135/api/products"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Custom-Header", "SpringRestClientDemo"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockProducts), MediaType.APPLICATION_JSON));

        List<ProductRecord> result = productApiClient.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Laptop");
        mockServer.verify();
    }

    @Test
    void testGetProductById() throws Exception {
        ProductRecord mockProduct = new ProductRecord(1L, "Laptop", 1200.0);

        mockServer.expect(requestTo("http://localhost:8135/api/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockProduct), MediaType.APPLICATION_JSON));

        ProductRecord result = productApiClient.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Laptop");
        mockServer.verify();
    }

    @Test
    void testGetProductById_NotFound() {
        mockServer.expect(requestTo("http://localhost:8135/api/products/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        CustomClientException exception = assertThrows(CustomClientException.class, () -> {
            productApiClient.getProductById(999L);
        });

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("Product not found");
        mockServer.verify();
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRecord newProduct = new ProductRecord(null, "Tablet", 400.0);
        ProductRecord savedProduct = new ProductRecord(3L, "Tablet", 400.0);

        mockServer.expect(requestTo("http://localhost:8135/api/products"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(newProduct)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(savedProduct), MediaType.APPLICATION_JSON));

        ProductRecord result = productApiClient.createProduct(newProduct);

        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.name()).isEqualTo("Tablet");
        mockServer.verify();
    }

    @Test
    void testHttpExchange_GetAll() throws Exception {
        List<ProductRecord> mockProducts = List.of(
                new ProductRecord(1L, "Monitor", 300.0)
        );

        mockServer.expect(requestTo("http://localhost:8135/api/products"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockProducts), MediaType.APPLICATION_JSON));

        List<ProductRecord> result = productHttpExchange.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Monitor");
        mockServer.verify();
    }

    @Test
    void testHttpExchange_Delete() {
        mockServer.expect(requestTo("http://localhost:8135/api/products/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        productHttpExchange.delete(1L);

        mockServer.verify();
    }
}
