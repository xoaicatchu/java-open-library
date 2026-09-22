package com.example.feign.client;

import com.example.feign.dto.ProductDto;
import com.example.feign.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoClientFallback implements FallbackFactory<DemoClient> {
    @Override
    public DemoClient create(Throwable cause) {
        if (cause instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) cause; // rethrow to pass test
        }
        System.out.println("Fallback cause: " + cause.getMessage());
        return new DemoClient() {
            @Override
            public List<ProductDto> getProducts(int limit) {
                return List.of(new ProductDto(-1L, "Fallback Product", 0.0));
            }

            @Override
            public ProductDto getProduct(Long id) {
                return new ProductDto(id, "Fallback Item", 0.0);
            }

            @Override
            public ProductDto createProduct(ProductDto product) {
                return new ProductDto(-2L, "Failed Creation", 0.0);
            }
        };
    }
}
