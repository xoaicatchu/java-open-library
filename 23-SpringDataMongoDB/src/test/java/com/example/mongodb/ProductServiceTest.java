package com.example.mongodb;

import com.example.mongodb.dto.CategoryCountDto;
import com.example.mongodb.dto.ProductAvgRatingDto;
import com.example.mongodb.entity.Product;
import com.example.mongodb.entity.Review;
import com.example.mongodb.repository.ProductRepository;
import com.example.mongodb.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // Prepare some data
        Product p1 = new Product("Laptop Pro", 1500.0, "Electronics", Arrays.asList("tech", "laptop"), Arrays.asList(
                new Review("Alice", 5, "Great!"),
                new Review("Bob", 4, "Good battery")
        ));
        p1.setId("p1");

        Product p2 = new Product("Smartphone Max", 1000.0, "Electronics", Arrays.asList("tech", "phone"), Arrays.asList(
                new Review("Charlie", 4, "Nice screen"),
                new Review("Dave", 2, "Too expensive")
        ));
        p2.setId("p2");

        Product p3 = new Product("Running Shoes", 120.0, "Sports", Arrays.asList("fitness", "shoes"), Arrays.asList(
                new Review("Eve", 5, "Very comfortable")
        ));
        p3.setId("p3");

        productRepository.saveAll(Arrays.asList(p1, p2, p3));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateAndReadProduct() {
        Product p = new Product("Monitor", 300.0, "Electronics", Arrays.asList("tech"), List.of());
        Product saved = productService.createProduct(p);
        
        assertThat(saved.getId()).isNotNull();
        
        Optional<Product> retrieved = productService.getProduct(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Monitor");
    }

    @Test
    void testUpdateProduct() {
        Product p = productService.getProduct("p3").orElseThrow();
        p.setPrice(100.0);
        productService.updateProduct(p);
        
        Product updated = productService.getProduct("p3").orElseThrow();
        assertThat(updated.getPrice()).isEqualTo(100.0);
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct("p3");
        assertThat(productService.getProduct("p3")).isEmpty();
    }

    @Test
    void testCustomQuery() {
        List<Product> products = productService.findByCategoryAndMinPrice("Electronics", 1200.0);
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop Pro");
    }

    @Test
    void testMongoTemplateCriteria() {
        List<Product> products = productService.findProductsByTag("tech");
        assertThat(products).hasSize(2);
    }

    @Test
    void testTextSearch() {
        List<Product> products = productService.searchProducts("Laptop");
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop Pro");
    }

    @Test
    void testAggregationCountByCategory() {
        List<CategoryCountDto> stats = productService.getProductsCountPerCategory();
        assertThat(stats).hasSize(2);
        
        CategoryCountDto electronics = stats.stream().filter(s -> "Electronics".equals(s.id())).findFirst().orElseThrow();
        assertThat(electronics.count()).isEqualTo(2);
    }

    @Test
    void testAggregationAverageRating() {
        List<ProductAvgRatingDto> stats = productService.getAverageRatingPerProduct();
        assertThat(stats).hasSize(3);
        
        ProductAvgRatingDto p1Stats = stats.stream().filter(s -> "p1".equals(s.id())).findFirst().orElseThrow();
        assertThat(p1Stats.avgRating()).isEqualTo(4.5);
    }
}
