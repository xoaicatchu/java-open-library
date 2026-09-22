package com.example.protobuf;

import com.example.protobuf.model.ProductOuterClass.Product;
import com.example.protobuf.model.ProductOuterClass.Category;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProtobufApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetProductAsProtobuf() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(new MediaType("application", "x-protobuf")));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Product> response = restTemplate.exchange(
                "/api/products/p1", HttpMethod.GET, request, Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().isCompatibleWith(new MediaType("application", "x-protobuf"))).isTrue();
        
        Product product = response.getBody();
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo("p1");
        assertThat(product.getName()).isEqualTo("Gaming Laptop");
        assertThat(product.getCategory().getName()).isEqualTo("Electronics");
        assertThat(product.getTagsList()).contains("gaming", "laptop");
        assertThat(product.getStatusCase().name()).isEqualTo("ACTIVE_STATUS");
    }

    @Test
    void testGetProductAsJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/products/p1", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        
        String jsonBody = response.getBody();
        assertThat(jsonBody).contains("\"id\": \"p1\"");
        assertThat(jsonBody).contains("\"name\": \"Gaming Laptop\"");
        assertThat(jsonBody).contains("\"category\"");
        assertThat(jsonBody).contains("\"activeStatus\": \"In Stock\"");
    }

    @Test
    void testPostProductAsProtobuf() {
        Instant now = Instant.now();
        Product newProduct = Product.newBuilder()
                .setId("p2")
                .setName("Mechanical Keyboard")
                .setPrice(120.00)
                .setCategory(Category.newBuilder().setId(2).setName("Accessories").build())
                .addTags("keyboard")
                .setCreatedAt(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
                .setDiscontinuedReason("Replaced by newer model")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "x-protobuf"));
        headers.setAccept(Collections.singletonList(new MediaType("application", "x-protobuf")));
        HttpEntity<Product> request = new HttpEntity<>(newProduct, headers);

        ResponseEntity<Product> response = restTemplate.postForEntity("/api/products", request, Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        Product returnedProduct = response.getBody();
        assertThat(returnedProduct).isNotNull();
        assertThat(returnedProduct.getId()).isEqualTo("p2");
        assertThat(returnedProduct.getDiscontinuedReason()).isEqualTo("Replaced by newer model");
        assertThat(returnedProduct.getStatusCase().name()).isEqualTo("DISCONTINUED_REASON");
    }

    @Test
    void testPostProductAsJson() {
        String jsonPayload = """
                {
                  "id": "p3",
                  "name": "Wireless Mouse",
                  "price": 50.0,
                  "category": {
                    "id": 2,
                    "name": "Accessories"
                  },
                  "tags": ["mouse", "wireless"],
                  "activeStatus": "Available"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"id\": \"p3\"");
        assertThat(response.getBody()).contains("\"name\": \"Wireless Mouse\"");
        assertThat(response.getBody()).contains("\"activeStatus\": \"Available\"");
    }

    @Test
    void testNotFound() {
        ResponseEntity<Product> response = restTemplate.getForEntity("/api/products/non-existent", Product.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
