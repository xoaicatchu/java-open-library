package com.example.r2dbc.controller;

import com.example.r2dbc.dto.ProductDto;
import com.example.r2dbc.entity.Product;
import com.example.r2dbc.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> create(@RequestBody ProductDto dto) {
        Product p = new Product(null, dto.name(), dto.price(), dto.stock());
        return productService.createProduct(p)
                .map(saved -> new ProductDto(saved.getId(), saved.getName(), saved.getPrice(), saved.getStock()));
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> get(@PathVariable Long id) {
        return productService.getProduct(id)
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStock()));
    }

    @GetMapping("/paged")
    public Flux<ProductDto> getPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsPaged(page, size)
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStock()));
    }

    @GetMapping("/low-stock")
    public Flux<ProductDto> getLowStock(@RequestParam Integer threshold) {
        return productService.getLowStockProducts(threshold)
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStock()));
    }

    @GetMapping("/price-range")
    public Flux<ProductDto> getByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return productService.findByPriceRange(min, max)
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStock()));
    }

    @PutMapping("/{id}/stock")
    public Mono<Void> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        return productService.updateStockFluent(id, stock).then();
    }
}
