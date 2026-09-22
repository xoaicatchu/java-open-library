package com.example.feign.controller;

import com.example.feign.dto.ProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam(required = false, defaultValue = "10") int limit) {
        if (limit > 5) {
            return List.of(
                new ProductDto(1L, "Laptop", 1200.0),
                new ProductDto(2L, "Mouse", 25.0)
            );
        }
        return List.of(new ProductDto(1L, "Laptop", 1200.0));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) {
        if ("error".equals(auth)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (id == 999) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProductDto(id, "Item " + id, 50.0));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductDto(100L, product.name(), product.price()));
    }
}
