package com.example.datajpa.specification;

import com.example.datajpa.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Product> priceGreaterThan(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThan(root.get("price"), minPrice);
    }
}
