package com.example.restclient.service;

import com.example.restclient.dto.ProductRecord;
import com.example.restclient.exception.CustomClientException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ProductApiClient {

    private final RestClient restClient;

    public ProductApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductRecord> getAllProducts() {
        return restClient.get()
                .uri("/api/products")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductRecord>>() {});
    }

    public ProductRecord getProductById(Long id) {
        return restClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomClientException((HttpStatus) response.getStatusCode(), "Product not found");
                })
                .body(ProductRecord.class);
    }

    public ProductRecord createProduct(ProductRecord record) {
        return restClient.post()
                .uri("/api/products")
                .body(record)
                .retrieve()
                .body(ProductRecord.class);
    }

    public void updateProduct(Long id, ProductRecord record) {
        restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}")
                        .queryParam("update", "true")
                        .build(id))
                .body(record)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new CustomClientException((HttpStatus) response.getStatusCode(), "Update failed");
                })
                .toBodilessEntity();
    }

    public void deleteProduct(Long id) {
        restClient.delete()
                .uri("/api/products/{id}")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomClientException((HttpStatus) response.getStatusCode(), "Delete failed");
                })
                .toBodilessEntity();
    }
}
