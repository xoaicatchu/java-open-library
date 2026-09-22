package com.example.webflux.controller;

import com.example.webflux.dto.ProductDto;
import com.example.webflux.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Flux<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        if (productDto.name() == null || productDto.name().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Product name must not be empty"));
        }
        return productService.createProduct(productDto);
    }

    // 3. Server-Sent Events (SSE) streaming endpoint
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDto> getProductStream() {
        return productService.getProductStream();
    }
    
    // 7. Backpressure demonstration
    @GetMapping(value = "/backpressure", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductDto> getProductsWithBackpressureDemo() {
        return productService.getProductsWithBackpressureDemo();
    }
}
