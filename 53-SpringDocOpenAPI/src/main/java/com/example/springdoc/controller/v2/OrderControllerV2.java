package com.example.springdoc.controller.v2;

import com.example.springdoc.dto.OrderRequest;
import com.example.springdoc.dto.OrderResponse;
import com.example.springdoc.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/orders")
@Tag(name = "Order API (v2)", description = "Quản lý đơn hàng version 2")
public class OrderControllerV2 {

    private final ApiService apiService;

    public OrderControllerV2(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách đơn hàng", description = "Trả về danh sách tất cả đơn hàng",
            security = @SecurityRequirement(name = "bearerAuth"))
    public List<OrderResponse> getAllOrders() {
        return apiService.getAllOrders();
    }

    @PostMapping
    @Operation(summary = "Tạo đơn hàng mới", description = "Thêm một đơn hàng mới vào hệ thống",
            security = @SecurityRequirement(name = "bearerAuth"))
    public OrderResponse createOrder(@RequestBody OrderRequest req) {
        return apiService.createOrder(req);
    }
}
