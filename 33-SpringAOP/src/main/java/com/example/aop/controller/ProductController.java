package com.example.aop.controller;

import com.example.aop.dto.ProductDto;
import com.example.aop.entity.AuditLog;
import com.example.aop.repository.AuditLogRepository;
import com.example.aop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private final AuditLogRepository auditLogRepository;

    public ProductController(ProductService productService, AuditLogRepository auditLogRepository) {
        this.productService = productService;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/api/products")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/api/products/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/api/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto dto) {
        return productService.createProduct(dto);
    }

    @DeleteMapping("/api/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/api/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }
}
