package com.example.springcache.controller;

import com.example.springcache.dto.ProductDto;
import com.example.springcache.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Lấy product - Sử dụng cache nếu có
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable Long id,
            @RequestParam(defaultValue = "en") String locale) {
        return ResponseEntity.ok(productService.getProductByIdAndLocale(id, locale));
    }

    // Tạo product mới
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    // Cập nhật product - Đồng thời cập nhật cache
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProductPrice(
            @PathVariable Long id,
            @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProductPrice(id, productDto.price()));
    }

    // Xóa cache của một product cụ thể
    @DeleteMapping("/{id}/cache")
    public ResponseEntity<Void> evictProductCache(@PathVariable Long id) {
        productService.evictProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Xóa toàn bộ cache products
    @DeleteMapping("/cache/all")
    public ResponseEntity<Void> evictAllProductCache() {
        productService.evictAllProducts();
        return ResponseEntity.noContent().build();
    }
}
