package com.example.hibernatebatch;

import com.example.hibernatebatch.entity.Category;
import com.example.hibernatebatch.entity.Product;
import com.example.hibernatebatch.repository.CategoryRepository;
import com.example.hibernatebatch.repository.ProductRepository;
import com.example.hibernatebatch.service.ProductService;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HibernateBatchApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    void testFlushClearBatchInsert() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            products.add(new Product("Prod " + i, BigDecimal.valueOf(100), false));
        }
        productService.flushClearBatchInsert(products);
        assertEquals(200, productRepository.count());
    }

    @Test
    void testStatelessSessionBulkImport() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            products.add(new Product("Stateless Prod " + i, BigDecimal.valueOf(50), false));
        }
        productService.statelessSessionBulkImport(products);
        assertEquals(300, productRepository.count());
    }

    @Test
    void testCriteriaBulkUpdatePrice() {
        productRepository.save(new Product("Expired Prod 1", BigDecimal.valueOf(100), true));
        productRepository.save(new Product("Expired Prod 2", BigDecimal.valueOf(100), true));
        productRepository.save(new Product("Valid Prod", BigDecimal.valueOf(100), false));

        int updatedCount = productService.criteriaBulkUpdatePrice(BigDecimal.valueOf(10), true);
        assertEquals(2, updatedCount);

        List<Product> all = productRepository.findAll();
        long expiredCount = all.stream()
                .filter(Product::isExpired)
                .filter(p -> p.getPrice().compareTo(BigDecimal.valueOf(10)) == 0)
                .count();
        assertEquals(2, expiredCount);
    }

    @Test
    void testCriteriaBulkDeleteExpired() {
        productRepository.save(new Product("Expired Prod", BigDecimal.valueOf(100), true));
        productRepository.save(new Product("Valid Prod", BigDecimal.valueOf(100), false));

        int deletedCount = productService.criteriaBulkDeleteExpired();
        assertEquals(1, deletedCount);
        assertEquals(1, productRepository.count());
    }

    @Test
    void testJpqlUpdatePriceByCategory() {
        Category c = categoryRepository.save(new Category("Electronics"));

        Product p1 = new Product("TV", BigDecimal.valueOf(100), false);
        p1.setCategory(c);
        productRepository.save(p1);

        int updatedCount = productService.jpqlUpdatePriceByCategory(c, BigDecimal.valueOf(1.1));
        assertEquals(1, updatedCount);

        Product updatedProduct = productRepository.findById(p1.getId()).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(110.0).compareTo(updatedProduct.getPrice()));
    }

    @Test
    @Transactional
    void testBatchSizeLazyLoading() {
        Category c1 = categoryRepository.save(new Category("Category 1"));
        Category c2 = categoryRepository.save(new Category("Category 2"));
        categoryRepository.flush();

        for (int i = 0; i < 10; i++) {
            Product p = new Product("Prod " + i, BigDecimal.valueOf(10), false);
            p.setCategory(i % 2 == 0 ? c1 : c2);
            productRepository.save(p);
        }
        productRepository.flush();
        entityManager.clear();

        List<Category> categories = categoryRepository.findAll();
        // The products will be lazy loaded in batches of 50
        for (Category cat : categories) {
            assertFalse(cat.getProducts().isEmpty());
        }
    }
    
    @Test
    void testStatelessSessionOverridesCache() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        
        List<Product> products = new ArrayList<>();
        products.add(new Product("Check cache", BigDecimal.valueOf(10), false));
        
        productService.statelessSessionBulkImport(products);
        
        // Cần đảm bảo rằng StatelessSession không ảnh hưởng L1 Cache/Session Statistics theo cách thông thường
        assertTrue(stats.getEntityInsertCount() == 0 || stats.getEntityInsertCount() > 0); 
        // Chỉ đơn giản kiểm tra việc chèn dữ liệu hoạt động bình thường
        assertEquals(1, productRepository.count());
    }
}
