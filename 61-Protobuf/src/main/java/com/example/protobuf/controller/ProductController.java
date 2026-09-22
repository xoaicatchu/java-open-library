package com.example.protobuf.controller;

import com.example.protobuf.model.ProductOuterClass.Product;
import com.example.protobuf.model.ProductOuterClass.Category;
import com.google.protobuf.Timestamp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Map<String, Product> productStore = new ConcurrentHashMap<>();

    public ProductController() {
        // Initialize with one dummy product
        Instant now = Instant.now();
        Product defaultProduct = Product.newBuilder()
                .setId("p1")
                .setName("Gaming Laptop")
                .setPrice(1500.00)
                .setCategory(Category.newBuilder().setId(1).setName("Electronics").build())
                .addTags("laptop")
                .addTags("gaming")
                .setCreatedAt(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
                .setActiveStatus("In Stock")
                .build();
        productStore.put(defaultProduct.getId(), defaultProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product = productStore.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public Collection<Product> getAllProducts() {
        return productStore.values();
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        productStore.put(product.getId(), product);
        return ResponseEntity.ok(product);
    }
}
