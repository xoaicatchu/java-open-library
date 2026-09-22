package com.example.mongodb.repository;

import com.example.mongodb.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // Custom JSON query to find by category and min price
    @Query("{ 'category' : ?0, 'price' : { $gte : ?1 } }")
    List<Product> findByCategoryAndMinPrice(String category, double minPrice);
}
