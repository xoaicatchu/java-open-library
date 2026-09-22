package com.example.r2dbc.repository;

import com.example.r2dbc.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    @Query("SELECT * FROM product WHERE stock < :threshold")
    Flux<Product> findByStockLessThan(Integer threshold);

    Flux<Product> findBy(Pageable pageable);
}
