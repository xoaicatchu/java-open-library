package com.example.mongodb.controller;

import com.example.mongodb.dto.CategoryCountDto;
import com.example.mongodb.dto.ProductAvgRatingDto;
import com.example.mongodb.entity.Product;
import com.example.mongodb.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productService.getProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())).body(createdProduct);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String text) {
        return productService.searchProducts(text);
    }

    @GetMapping("/analytics/category-counts")
    public List<CategoryCountDto> getCategoryCounts() {
        return productService.getProductsCountPerCategory();
    }

    @GetMapping("/analytics/avg-ratings")
    public List<ProductAvgRatingDto> getAverageRatings() {
        return productService.getAverageRatingPerProduct();
    }
}
