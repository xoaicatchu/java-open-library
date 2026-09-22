package com.example.webmvc.controller;

import com.example.webmvc.dto.CreateProductRequest;
import com.example.webmvc.dto.ProductResponse;
import com.example.webmvc.dto.UpdateProductRequest;
import com.example.webmvc.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST Controller quản lý Product.
 *
 * Minh họa:
 * - @RestController + @RequestMapping
 * - ResponseEntity<T> với đúng HTTP status code
 * - @Valid cho request validation
 * - Pageable cho phân trang
 * - Location header cho POST (201 Created)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products?page=0&size=10&sort=name,asc
     * Lấy danh sách sản phẩm có phân trang.
     */
    @GetMapping
    public Page<ProductResponse> getAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return productService.findAll(pageable);
    }

    /**
     * GET /api/products/{id}
     * Lấy chi tiết một sản phẩm.
     */
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    /**
     * GET /api/products/category/{category}
     * Lấy sản phẩm theo danh mục.
     */
    @GetMapping("/category/{category}")
    public List<ProductResponse> getByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    /**
     * GET /api/products/search?keyword=laptop&page=0&size=10
     * Tìm kiếm sản phẩm theo tên.
     */
    @GetMapping("/search")
    public Page<ProductResponse> search(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return productService.search(keyword, pageable);
    }

    /**
     * POST /api/products
     * Tạo sản phẩm mới. Trả về 201 Created + Location header.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /api/products/{id}
     * Cập nhật toàn bộ sản phẩm.
     */
    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    /**
     * DELETE /api/products/{id}
     * Xóa sản phẩm. Trả về 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
