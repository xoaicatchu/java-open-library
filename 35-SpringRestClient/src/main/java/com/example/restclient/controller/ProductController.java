package com.example.restclient.controller;

import com.example.restclient.dto.ProductRecord;
import com.example.restclient.entity.Product;
import com.example.restclient.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<ProductRecord> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductRecord(p.getId(), p.getName(), p.getPrice()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRecord> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(p -> ResponseEntity.ok(new ProductRecord(p.getId(), p.getName(), p.getPrice())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductRecord createProduct(@RequestBody ProductRecord record) {
        Product p = new Product(record.name(), record.price());
        Product saved = productRepository.save(p);
        return new ProductRecord(saved.getId(), saved.getName(), saved.getPrice());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductRecord> updateProduct(@PathVariable Long id, @RequestBody ProductRecord record) {
        return productRepository.findById(id)
                .map(p -> {
                    p.setName(record.name());
                    p.setPrice(record.price());
                    Product saved = productRepository.save(p);
                    return ResponseEntity.ok(new ProductRecord(saved.getId(), saved.getName(), saved.getPrice()));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
