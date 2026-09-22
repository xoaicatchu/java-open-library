package com.example.springdoc.service;

import com.example.springdoc.dto.OrderRequest;
import com.example.springdoc.dto.OrderResponse;
import com.example.springdoc.dto.ProductRequest;
import com.example.springdoc.dto.ProductResponse;
import com.example.springdoc.entity.Order;
import com.example.springdoc.entity.Product;
import com.example.springdoc.repository.OrderRepository;
import com.example.springdoc.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ApiService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ApiService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest req) {
        Product p = new Product(req.name(), req.price());
        p = productRepository.save(p);
        return new ProductResponse(p.getId(), p.getName(), p.getPrice());
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .orElse(null);
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest req) {
        Order o = new Order(req.customerName(), req.productId(), req.quantity());
        o = orderRepository.save(o);
        return new OrderResponse(o.getId(), o.getCustomerName(), o.getProductId(), o.getQuantity());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(o -> new OrderResponse(o.getId(), o.getCustomerName(), o.getProductId(), o.getQuantity()))
                .toList();
    }
}
