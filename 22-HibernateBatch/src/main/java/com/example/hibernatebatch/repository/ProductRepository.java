package com.example.hibernatebatch.repository;

import com.example.hibernatebatch.entity.Category;
import com.example.hibernatebatch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("UPDATE Product p SET p.price = p.price * :multiplier WHERE p.category = :category")
    int updatePriceByCategory(@Param("category") Category category, @Param("multiplier") BigDecimal multiplier);
}
