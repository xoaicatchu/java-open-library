package com.example.feign.client;

import com.example.feign.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Interacts with the local server to act as a "remote" service
@FeignClient(name = "demoClient", url = "http://localhost:8134", fallbackFactory = DemoClientFallback.class, configuration = FeignConfig.class)
public interface DemoClient {

    @GetMapping("/api/products")
    List<ProductDto> getProducts(@RequestParam("limit") int limit);

    @GetMapping("/api/products/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);

    @PostMapping("/api/products")
    ProductDto createProduct(@RequestBody ProductDto product);
}
