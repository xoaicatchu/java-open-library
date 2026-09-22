package com.example.caffeine.controller;

import com.example.caffeine.dto.ProductDto;
import com.example.caffeine.service.ProductService;
import org.springframework.web.bind.annotation.*;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto dto) {
        return productService.createProduct(dto);
    }

    @GetMapping("/spring/{id}")
    public ProductDto getSpringCache(@PathVariable Long id) {
        return productService.getProductSpringCache(id);
    }

    @GetMapping("/manual/{id}")
    public ProductDto getManualCache(@PathVariable Long id) {
        return productService.getProductManualCache(id);
    }

    @GetMapping("/loading/{id}")
    public ProductDto getLoadingCache(@PathVariable Long id) {
        return productService.getProductLoadingCache(id);
    }

    @GetMapping("/async/{id}")
    public CompletableFuture<ProductDto> getAsyncCache(@PathVariable Long id) {
        return productService.getProductAsyncCache(id);
    }

    @DeleteMapping("/spring/{id}")
    public void evictSpringCache(@PathVariable Long id) {
        productService.evictSpringCache(id);
    }

    @DeleteMapping("/manual/{id}")
    public void evictManualCache(@PathVariable Long id) {
        productService.evictManualCache(id);
    }

    @GetMapping("/manual/stats")
    public String getManualStats() {
        CacheStats stats = productService.getManualCacheStats();
        return stats.toString();
    }
}
