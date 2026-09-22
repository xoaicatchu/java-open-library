package com.example.aop;

import com.example.aop.dto.ProductDto;
import com.example.aop.entity.AuditLog;
import com.example.aop.exception.RateLimitExceededException;
import com.example.aop.repository.AuditLogRepository;
import com.example.aop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AopApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateProductAndAuditLog() {
        ProductDto dto = new ProductDto(null, "Laptop", new BigDecimal("1200.0"));
        ProductDto created = productService.createProduct(dto);
        
        assertThat(created.id()).isNotNull();
        
        // Verify AuditAspect fired
        List<AuditLog> logs = auditLogRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getAction()).isEqualTo("CREATE_PRODUCT");
    }
    
    @Test
    void testLoggingAspectBeforeAfterReturning() {
        // This will trigger @Before, @Around, @After, @AfterReturning
        List<ProductDto> products = productService.getAllProducts();
        assertThat(products).isNotNull();
        // Check logs in console output to see LoggingAspect and PerformanceAspect
    }
    
    @Test
    void testLoggingAspectAfterThrowing() {
        // This will trigger @AfterThrowing
        assertThatThrownBy(() -> productService.getProductWithError(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }
    
    @Test
    void testRateLimitEnforcement() {
        // Method is configured with max 3 requests per 10 seconds
        String res1 = productService.getSensitiveData();
        String res2 = productService.getSensitiveData();
        String res3 = productService.getSensitiveData();
        
        assertThat(res1).isEqualTo("Sensitive Data");
        
        assertThatThrownBy(() -> productService.getSensitiveData())
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("Rate limit exceeded for method: getSensitiveData");
    }
    
    @Test
    void testDeleteProductAuditLog() {
        ProductDto dto = new ProductDto(null, "Mouse", new BigDecimal("20.0"));
        ProductDto created = productService.createProduct(dto);
        
        productService.deleteProduct(created.id());
        
        // Should have 2 logs: CREATE_PRODUCT and DELETE_PRODUCT
        List<AuditLog> logs = auditLogRepository.findAll();
        assertThat(logs).hasSize(2);
        assertThat(logs).anyMatch(log -> "DELETE_PRODUCT".equals(log.getAction()));
    }
}
