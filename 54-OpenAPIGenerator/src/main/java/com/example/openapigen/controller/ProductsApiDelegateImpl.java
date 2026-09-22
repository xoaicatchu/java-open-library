package com.example.openapigen.controller;

import com.example.openapigen.api.ProductsApiDelegate;
import com.example.openapigen.model.CreateProductRequest;
import com.example.openapigen.model.Product;
import com.example.openapigen.model.UpdateProductRequest;
import com.example.openapigen.model.ErrorResponse;
import com.example.openapigen.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductsApiDelegateImpl implements ProductsApiDelegate {

    private final ProductService productService;

    public ProductsApiDelegateImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Override
    public ResponseEntity<Product> createProduct(CreateProductRequest createProductRequest) {
        Product created = productService.createProduct(createProductRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Product> getProductById(Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<Product> updateProduct(Long id, UpdateProductRequest updateProductRequest) {
        Optional<Product> updated = productService.updateProduct(id, updateProductRequest);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
