package com.example.hibernatebatch.controller;

import com.example.hibernatebatch.dto.ProductDto;
import com.example.hibernatebatch.entity.Category;
import com.example.hibernatebatch.entity.Product;
import com.example.hibernatebatch.repository.CategoryRepository;
import com.example.hibernatebatch.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public BatchController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importProducts(@RequestParam(defaultValue = "100") int count) {
        Category cat = categoryRepository.findByName("Electronics")
                .orElseGet(() -> categoryRepository.save(new Category("Electronics")));

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product p = new Product("Product " + i, BigDecimal.valueOf(10 + (i % 10)), false);
            p.setCategory(cat);
            products.add(p);
        }
        productService.statelessSessionBulkImport(products);
        return ResponseEntity.ok("Imported " + count + " products");
    }

    @PutMapping("/update-prices")
    public ResponseEntity<String> updatePrices(
            @RequestParam String category,
            @RequestParam int percentage) {
        return categoryRepository.findByName(category).map(cat -> {
            BigDecimal multiplier = BigDecimal.valueOf(1 + (percentage / 100.0));
            int updated = productService.jpqlUpdatePriceByCategory(cat, multiplier);
            return ResponseEntity.ok("Updated " + updated + " products");
        }).orElseGet(() -> ResponseEntity.badRequest().body("Category not found"));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanup(@RequestParam BigDecimal maxPrice) {
        int deleted = productService.criteriaBulkDeleteByMaxPrice(maxPrice);
        return ResponseEntity.ok("Deleted " + deleted + " products with price <= " + maxPrice);
    }

    @GetMapping("/products")
    public List<ProductDto> getProducts() {
        return productService.getAllProducts().stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.isExpired(),
                        p.getCategory() != null ? p.getCategory().getName() : null
                ))
                .collect(Collectors.toList());
    }
}
