package com.example.datajpa.repository;

import com.example.datajpa.entity.Product;
import com.example.datajpa.projection.ProductSummary;
import com.example.datajpa.projection.ProductSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // 1. Query derivation
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    // 2. @Query - JPQL
    @Query("SELECT p FROM Product p WHERE p.price > :price")
    List<Product> findProductsMoreExpensiveThan(@Param("price") BigDecimal price);

    // 3. @Query - Native
    @Query(value = "SELECT * FROM product WHERE name = :name", nativeQuery = true)
    List<Product> findProductsByNameNative(@Param("name") String name);

    // 4. Projection - Interface based
    List<ProductSummary> findSummaryByNameContaining(String name);

    // 5. Projection - DTO based
    @Query("SELECT new com.example.datajpa.projection.ProductSummaryDto(p.name, p.price) FROM Product p WHERE p.name LIKE %:name%")
    List<ProductSummaryDto> findDtoByNameContaining(@Param("name") String name);

    // 6. EntityGraph - N+1 prevention
    @EntityGraph(value = "Product.category", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT p FROM Product p")
    List<Product> findAllWithCategory();

    @EntityGraph(attributePaths = {"category", "orderItems"})
    Optional<Product> findWithDetailsById(Long id);

    // 7. Pagination
    Page<Product> findByPriceGreaterThan(BigDecimal price, Pageable pageable);
}
