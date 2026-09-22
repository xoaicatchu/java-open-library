package com.example.hibernatebatch.service;

import com.example.hibernatebatch.entity.Category;
import com.example.hibernatebatch.entity.Product;
import com.example.hibernatebatch.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public ProductService(ProductRepository productRepository, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    /**
     * JDBC Batch Insert sử dụng flush() và clear() để tránh OutOfMemoryError.
     * Cần cấu hình spring.jpa.properties.hibernate.jdbc.batch_size=50 trong properties.
     */
    @Transactional
    public void flushClearBatchInsert(List<Product> products) {
        int batchSize = 50;
        for (int i = 0; i < products.size(); i++) {
            entityManager.persist(products.get(i));
            if (i > 0 && i % batchSize == 0) {
                // Xả dữ liệu xuống DB
                entityManager.flush();
                // Xóa khỏi L1 Cache để giải phóng bộ nhớ
                entityManager.clear();
            }
        }
    }

    /**
     * Bulk import sử dụng StatelessSession của Hibernate.
     * Bỏ qua L1 Cache, L2 Cache, interceptors, và các sự kiện để đạt hiệu năng tối đa.
     */
    @Transactional
    public void statelessSessionBulkImport(List<Product> products) {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        // StatelessSession cung cấp kết nối trực tiếp cấp thấp đến cơ sở dữ liệu
        try (StatelessSession statelessSession = sessionFactory.openStatelessSession()) {
            statelessSession.beginTransaction();
            for (Product product : products) {
                statelessSession.insert(product);
            }
            statelessSession.getTransaction().commit();
        }
    }

    /**
     * Bulk update sử dụng Criteria API. Thực thi trực tiếp dưới database mà không tải entities lên memory.
     */
    @Transactional
    public int criteriaBulkUpdatePrice(BigDecimal newPrice, boolean expired) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Product> update = cb.createCriteriaUpdate(Product.class);
        Root<Product> root = update.from(Product.class);

        update.set("price", newPrice);
        update.where(cb.equal(root.get("expired"), expired));

        return entityManager.createQuery(update).executeUpdate();
    }

    /**
     * Bulk delete sử dụng Criteria API. Thực thi trực tiếp dưới database.
     */
    @Transactional
    public int criteriaBulkDeleteExpired() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Product> delete = cb.createCriteriaDelete(Product.class);
        Root<Product> root = delete.from(Product.class);

        delete.where(cb.isTrue(root.get("expired")));

        return entityManager.createQuery(delete).executeUpdate();
    }

    /**
     * Bulk update thông qua @Query (JPQL) trong Repository.
     */
    @Transactional
    public int jpqlUpdatePriceByCategory(Category category, BigDecimal multiplier) {
        return productRepository.updatePriceByCategory(category, multiplier);
    }

    @Transactional
    public int criteriaBulkDeleteByMaxPrice(BigDecimal maxPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Product> delete = cb.createCriteriaDelete(Product.class);
        Root<Product> root = delete.from(Product.class);
        delete.where(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        return entityManager.createQuery(delete).executeUpdate();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategory();
    }
}
