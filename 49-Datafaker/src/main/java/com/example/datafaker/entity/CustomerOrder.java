package com.example.datafaker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    
    @ManyToOne
    private Customer customer;

    public CustomerOrder() {}
    public CustomerOrder(String orderNumber, LocalDate orderDate, Customer customer) {
        this.orderNumber = orderNumber; this.orderDate = orderDate; this.customer = customer;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
