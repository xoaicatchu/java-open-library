package com.example.r2dbc.service;

import com.example.r2dbc.entity.Product;
import com.example.r2dbc.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate entityTemplate;

    public ProductService(ProductRepository productRepository, DatabaseClient databaseClient, R2dbcEntityTemplate entityTemplate) {
        this.productRepository = productRepository;
        this.databaseClient = databaseClient;
        this.entityTemplate = entityTemplate;
    }

    @Transactional
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    public Flux<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockLessThan(threshold);
    }

    public Flux<Product> getProductsPaged(int page, int size) {
        return productRepository.findBy(PageRequest.of(page, size, Sort.by("name")));
    }

    public Flux<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return databaseClient.sql("SELECT * FROM product WHERE price BETWEEN :min AND :max")
                .bind("min", min)
                .bind("max", max)
                .mapProperties(Product.class)
                .all();
    }

    @Transactional
    public Mono<Long> updateStockFluent(Long id, Integer newStock) {
        return databaseClient.sql("UPDATE product SET stock = :stock WHERE id = :id")
                .bind("stock", newStock)
                .bind("id", id)
                .fetch()
                .rowsUpdated();
    }

    @Transactional
    public Mono<Product> saveWithTemplate(Product product) {
        return entityTemplate.insert(Product.class).using(product);
    }
}
