package com.example.webflux.service;

import com.example.webflux.dto.ProductDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class WebClientDemoService {

    private final WebClient webClient;

    public WebClientDemoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8102").build();
    }

    // 5. WebClient for reactive HTTP calls
    public Flux<ProductDto> fetchProducts() {
        return webClient.get()
                .uri("/api/products")
                .retrieve()
                .bodyToFlux(ProductDto.class);
    }
}
