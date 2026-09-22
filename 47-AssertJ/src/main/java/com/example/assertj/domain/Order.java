package com.example.assertj.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Order {
    private String orderId;
    private Customer customer;
    private List<Product> items;
    private Map<String, String> metadata;
    private LocalDateTime orderDate;

    public Order(String orderId, Customer customer, List<Product> items, Map<String, String> metadata, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.metadata = metadata;
        this.orderDate = orderDate;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<Product> getItems() { return items; }
    public void setItems(List<Product> items) { this.items = items; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public void validate() {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }
}
