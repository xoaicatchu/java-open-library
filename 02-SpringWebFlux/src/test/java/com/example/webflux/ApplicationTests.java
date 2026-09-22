package com.example.webflux;

import com.example.webflux.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetAllProducts() {
        webTestClient.get().uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .consumeWith(result -> {
                    assert result.getResponseBody() != null;
                    assert !result.getResponseBody().isEmpty();
                });
    }

    @Test
    void testGetProductById() {
        webTestClient.get().uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDto.class)
                .value(product -> {
                    assertThat(product.name()).isEqualTo("Laptop");
                });
    }

    @Test
    void testGetProductNotFound() {
        webTestClient.get().uri("/api/products/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .value(detail -> {
                    assertThat(detail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                    assertThat(detail.getDetail()).contains("Product not found");
                });
    }

    @Test
    void testCreateProduct() {
        ProductDto newProduct = new ProductDto(null, "Tablet", 300.0);
        
        webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .value(product -> {
                    assertThat(product.id()).isNotNull();
                    assertThat(product.name()).isEqualTo("Tablet");
                });
    }

    @Test
    void testCreateProductInvalid() {
        ProductDto newProduct = new ProductDto(null, "", 300.0); // Empty name
        
        webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(detail -> {
                    assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(detail.getDetail()).contains("Product name must not be empty");
                });
    }

    @Test
    void testFunctionalEndpointGetAll() {
        webTestClient.get().uri("/functional/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .consumeWith(result -> {
                    assert result.getResponseBody() != null;
                    assert result.getResponseBody().size() >= 3;
                });
    }

    @Test
    void testFunctionalEndpointGetById() {
        webTestClient.get().uri("/functional/products/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDto.class)
                .value(product -> {
                    assertThat(product.name()).isEqualTo("Smartphone");
                });
    }

    @Test
    void testFunctionalEndpointNotFound() {
        webTestClient.get().uri("/functional/products/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testSseStream() {
        webTestClient.get().uri("/api/products/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(ProductDto.class)
                .getResponseBody()
                .take(3)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }
    
    @Test
    void testBackpressure() {
        webTestClient.get().uri("/api/products/backpressure")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody()
                .take(2)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }
}
