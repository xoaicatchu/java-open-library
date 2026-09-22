package com.example.jooq.controller;

import com.example.jooq.dto.CategoryRevenueDto;
import com.example.jooq.dto.ProductDto;
import com.example.jooq.dto.ProductRankDto;
import com.example.jooq.dto.SaleDto;
import com.example.jooq.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/products")
    public List<ProductDto> getAllProducts() {
        return analyticsService.getAllProducts();
    }

    @GetMapping("/revenue")
    public List<CategoryRevenueDto> getRevenueByCategory() {
        return analyticsService.getRevenueByCategory();
    }

    @GetMapping("/ranking")
    public List<ProductRankDto> getProductRankingByRevenue() {
        return analyticsService.getProductRankingByRevenue();
    }

    @GetMapping("/top")
    public List<ProductDto> getTopPerformingProducts() {
        return analyticsService.getTopPerformingProducts();
    }

    @PostMapping("/sales/batch")
    public ResponseEntity<Void> batchInsertSales(@RequestBody List<SaleDto> newSales) {
        analyticsService.batchInsertSales(newSales);
        return ResponseEntity.ok().build();
    }
}
