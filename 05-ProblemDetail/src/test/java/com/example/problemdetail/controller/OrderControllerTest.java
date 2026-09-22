package com.example.problemdetail.controller;

import com.example.problemdetail.dto.OrderItemRequest;
import com.example.problemdetail.dto.OrderRequest;
import com.example.problemdetail.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenOrderNotFound_thenReturns404ProblemDetail() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/probs/order-not-found"))
                .andExpect(jsonPath("$.title").value("Order Not Found"))
                .andExpect(jsonPath("$.detail").value("Không tìm thấy đơn hàng với ID 999"))
                .andExpect(jsonPath("$.orderId").value(999))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void whenValidationFails_thenReturns400ProblemDetailWithFieldErrors() throws Exception {
        OrderRequest invalidRequest = new OrderRequest(null, null);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/probs/validation-error"))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.invalidParams.customerId").exists())
                .andExpect(jsonPath("$.invalidParams.items").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void whenInsufficientStock_thenReturns422ProblemDetail() throws Exception {
        OrderRequest request = new OrderRequest("CUST-1", List.of(
            new OrderItemRequest("OUT_OF_STOCK_ITEM", 1, new BigDecimal("100.00"))
        ));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/probs/insufficient-stock"))
                .andExpect(jsonPath("$.title").value("Insufficient Stock"))
                .andExpect(jsonPath("$.detail").value("Số lượng tồn kho không đủ cho sản phẩm OUT_OF_STOCK_ITEM"))
                .andExpect(jsonPath("$.productId").value("OUT_OF_STOCK_ITEM"));
    }

    @Test
    void whenOrderAlreadyCancelled_thenReturns409ProblemDetail() throws Exception {
        OrderRequest request = new OrderRequest("CUST-2", List.of(
            new OrderItemRequest("ITEM1", 1, new BigDecimal("100.00"))
        ));
        
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        Order order = objectMapper.readValue(response, Order.class);
        
        mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel"))
                .andExpect(status().isNoContent());
                
        mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/probs/order-already-cancelled"))
                .andExpect(jsonPath("$.title").value("Order Already Cancelled"));
    }
    
    @Test
    void whenPaymentDeclined_thenReturns402ProblemDetail() throws Exception {
        OrderRequest request = new OrderRequest("CUST-3", List.of(
            new OrderItemRequest("ITEM1", 1, new BigDecimal("100.00"))
        ));
        
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        Order order = objectMapper.readValue(response, Order.class);
        
        mockMvc.perform(post("/api/orders/" + order.getId() + "/pay")
                .param("reasonCode", "INSUFFICIENT_FUNDS"))
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/probs/payment-declined"))
                .andExpect(jsonPath("$.title").value("Payment Declined"))
                .andExpect(jsonPath("$.detail").value("Thanh toán bị từ chối với lý do: INSUFFICIENT_FUNDS"))
                .andExpect(jsonPath("$.reason").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void validOrderCreationReturns201() throws Exception {
        OrderRequest request = new OrderRequest("CUST-OK", List.of(
            new OrderItemRequest("ITEM1", 2, new BigDecimal("50.00"))
        ));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(100.0));
    }
    
    @Test
    void getValidOrderReturns200() throws Exception {
        OrderRequest request = new OrderRequest("CUST-OK2", List.of(
            new OrderItemRequest("ITEM1", 1, new BigDecimal("50.00"))
        ));

        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        Order order = objectMapper.readValue(response, Order.class);

        mockMvc.perform(get("/api/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.customerId").value("CUST-OK2"));
    }
}