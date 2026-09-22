package com.example.problemdetail.repository;

import com.example.problemdetail.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}