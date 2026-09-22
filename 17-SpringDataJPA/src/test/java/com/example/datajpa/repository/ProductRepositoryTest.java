package com.example.datajpa.repository;

import com.example.datajpa.entity.Category;
import com.example.datajpa.entity.OrderItem;
import com.example.datajpa.entity.Product;
import com.example.datajpa.projection.ProductSummary;
import com.example.datajpa.projection.ProductSummaryDto;
import com.example.datajpa.specification.ProductSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import com.example.datajpa.config.JpaConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category electronics;

    @BeforeEach
    void setUp() {
        electronics = new Category();
        electronics.setName("Electronics");
        categoryRepository.saveAndFlush(electronics);

        Product p1 = new Product();
        p1.setName("Laptop");
        p1.setPrice(new BigDecimal("1200.00"));
        electronics.addProduct(p1);

        Product p2 = new Product();
        p2.setName("Smartphone");
        p2.setPrice(new BigDecimal("800.00"));
        electronics.addProduct(p2);

        productRepository.saveAllAndFlush(List.of(p1, p2));
        entityManager.clear();
    }

    @Test
    void testCrudOperations() {
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);

        Product product = products.get(0);
        product.setPrice(new BigDecimal("1300.00"));
        productRepository.saveAndFlush(product);

        Optional<Product> updated = productRepository.findById(product.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getPrice()).isEqualByComparingTo("1300.00");
    }

    @Test
    void testQueryDerivation() {
        List<Product> laptops = productRepository.findByNameContaining("Lap");
        assertThat(laptops).hasSize(1);
        assertThat(laptops.get(0).getName()).isEqualTo("Laptop");

        List<Product> between = productRepository.findByPriceBetween(new BigDecimal("700.00"), new BigDecimal("1000.00"));
        assertThat(between).hasSize(1);
        assertThat(between.get(0).getName()).isEqualTo("Smartphone");
    }

    @Test
    void testJpqlAndNativeQuery() {
        List<Product> expensive = productRepository.findProductsMoreExpensiveThan(new BigDecimal("1000.00"));
        assertThat(expensive).hasSize(1);
        assertThat(expensive.get(0).getName()).isEqualTo("Laptop");

        List<Product> nativeResult = productRepository.findProductsByNameNative("Smartphone");
        assertThat(nativeResult).hasSize(1);
    }

    @Test
    void testSpecifications() {
        List<Product> result = productRepository.findAll(
                ProductSpecification.hasName("phone")
                        .and(ProductSpecification.priceGreaterThan(new BigDecimal("500.00")))
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Smartphone");
    }

    @Test
    void testPaginationAndSorting() {
        Page<Product> page = productRepository.findByPriceGreaterThan(
                new BigDecimal("500.00"),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "price"))
        );
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Laptop"); // Higher price first
    }

    @Test
    void testEntityGraph() {
        List<Product> products = productRepository.findAllWithCategory();
        assertThat(products).isNotEmpty();
        assertThat(products.get(0).getCategory().getName()).isEqualTo("Electronics");
    }

    @Test
    void testProjections() {
        List<ProductSummary> summaries = productRepository.findSummaryByNameContaining("Lap");
        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).getName()).isEqualTo("Laptop");
        assertThat(summaries.get(0).getCategory().getName()).isEqualTo("Electronics");

        List<ProductSummaryDto> dtos = productRepository.findDtoByNameContaining("phone");
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).name()).isEqualTo("Smartphone");
    }

    @Test
    void testAuditing() {
        Product p = new Product();
        p.setName("Tablet");
        p.setPrice(new BigDecimal("400.00"));
        
        Product saved = productRepository.saveAndFlush(p);
        
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getLastModifiedDate()).isNotNull();
        assertThat(saved.getCreatedBy()).isEqualTo("system_user");
        assertThat(saved.getLastModifiedBy()).isEqualTo("system_user");
    }

    @Test
    void testOptimisticLocking() {
        Product p = productRepository.findAll().get(0);
        Long id = p.getId();
        Long oldVersion = p.getVersion();
        
        // Bump version in DB
        p.setPrice(new BigDecimal("1500.00"));
        productRepository.saveAndFlush(p);
        
        // Create stale detached entity
        Product stale = new Product();
        stale.setId(id);
        stale.setVersion(oldVersion);
        stale.setName(p.getName());
        stale.setPrice(new BigDecimal("1600.00"));
        
        assertThatThrownBy(() -> productRepository.saveAndFlush(stale))
                .isInstanceOf(org.springframework.orm.ObjectOptimisticLockingFailureException.class);
    }
    
    @Test
    void testRelationships() {
        Product p = productRepository.findAll().get(0);
        OrderItem item = new OrderItem();
        item.setQuantity(2);
        p.addOrderItem(item);
        
        productRepository.saveAndFlush(p);
        entityManager.clear();
        
        Optional<Product> withDetails = productRepository.findWithDetailsById(p.getId());
        assertThat(withDetails).isPresent();
        assertThat(withDetails.get().getOrderItems()).hasSize(1);
        assertThat(withDetails.get().getOrderItems().get(0).getQuantity()).isEqualTo(2);
    }
}
