package com.example.springdoc.controller.v1;

import com.example.springdoc.dto.ProductRequest;
import com.example.springdoc.dto.ProductResponse;
import com.example.springdoc.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API (v1)", description = "Quản lý sản phẩm version 1")
public class ProductControllerV1 {

    private final ApiService apiService;

    public ProductControllerV1(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Trả về danh sách tất cả sản phẩm")
    public List<ProductResponse> getAllProducts() {
        return apiService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin sản phẩm", description = "Tìm kiếm sản phẩm theo ID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse res = apiService.getProduct(id);
        if (res == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(res);
    }

    @PostMapping
    @Operation(summary = "Tạo sản phẩm mới", description = "Thêm một sản phẩm mới vào hệ thống",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ProductResponse createProduct(@RequestBody ProductRequest req) {
        return apiService.createProduct(req);
    }
}
