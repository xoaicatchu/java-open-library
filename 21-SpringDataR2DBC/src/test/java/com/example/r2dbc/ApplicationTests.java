package com.example.r2dbc;

import com.example.r2dbc.dto.ProductDto;
import com.example.r2dbc.entity.Product;
import com.example.r2dbc.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        databaseClient.sql("DELETE FROM product").fetch().rowsUpdated().block();
        
        productService.createProduct(new Product(null, "Laptop", new BigDecimal("1000.00"), 50)).block();
        productService.createProduct(new Product(null, "Mouse", new BigDecimal("25.00"), 200)).block();
        productService.createProduct(new Product(null, "Keyboard", new BigDecimal("75.00"), 10)).block();
    }

    @Test
    void testCreateAndGetProduct() {
        ProductDto newProduct = new ProductDto(null, "Monitor", new BigDecimal("300.00"), 15);

        webTestClient.post()
                .uri("/products")
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Monitor");
    }

    @Test
    void testGetLowStockProducts() {
        webTestClient.get()
                .uri("/products/low-stock?threshold=20")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .hasSize(1)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody().get(0).name()).isEqualTo("Keyboard");
                });
    }

    @Test
    void testGetProductsPaged() {
        webTestClient.get()
                .uri("/products/paged?page=0&size=2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .hasSize(2);
    }

    @Test
    void testFindByPriceRangeUsingDatabaseClient() {
        webTestClient.get()
                .uri("/products/price-range?min=20.00&max=100.00")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .hasSize(2);
    }

    @Test
    void testUpdateStockFluent() {
        Product product = productService.getLowStockProducts(20).blockFirst();
        assertThat(product).isNotNull();

        webTestClient.put()
                .uri("/products/{id}/stock?stock=500", product.getId())
                .exchange()
                .expectStatus().isOk();

        Product updated = productService.getProduct(product.getId()).block();
        assertThat(updated.getStock()).isEqualTo(500);
    }

    @Test
    void testSaveWithTemplate() {
        Product p = new Product(null, "Tablet", new BigDecimal("500.00"), 30);
        Mono<Product> saved = productService.saveWithTemplate(p);
        
        StepVerifier.create(saved)
                .assertNext(product -> {
                    assertThat(product.getId()).isNotNull();
                    assertThat(product.getName()).isEqualTo("Tablet");
                })
                .verifyComplete();
    }
}
