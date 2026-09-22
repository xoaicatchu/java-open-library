package com.example.jooq;

import com.example.jooq.dto.CategoryRevenueDto;
import com.example.jooq.dto.ProductDto;
import com.example.jooq.dto.ProductRankDto;
import com.example.jooq.dto.SaleDto;
import com.example.jooq.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AnalyticsServiceTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Test
    void testGetAllProducts() {
        List<ProductDto> products = analyticsService.getAllProducts();
        assertThat(products).hasSize(5);
    }

    @Test
    void testJoins_GetRevenueByCategory() {
        List<CategoryRevenueDto> revenue = analyticsService.getRevenueByCategory();
        assertThat(revenue).isNotEmpty();
        assertThat(revenue.get(0).categoryName()).isEqualTo("Electronics");
        assertThat(revenue.get(0).totalRevenue()).isEqualByComparingTo(new BigDecimal("6500.00"));
    }

    @Test
    void testWindowFunctions_GetProductRanking() {
        List<ProductRankDto> ranks = analyticsService.getProductRankingByRevenue();
        assertThat(ranks).isNotEmpty();
        assertThat(ranks.get(0).productName()).isEqualTo("Phone");
        assertThat(ranks.get(0).rank()).isEqualTo(1);
    }

    @Test
    void testCte_GetTopPerformingProducts() {
        List<ProductDto> topProducts = analyticsService.getTopPerformingProducts();
        assertThat(topProducts).hasSize(2);
        assertThat(topProducts).extracting(ProductDto::name)
                .containsExactly("Phone", "Laptop");
    }

    @Test
    void testBatchOperations() {
        List<SaleDto> batch = List.of(
            new SaleDto(null, 1, 1, LocalDate.now(), new BigDecimal("1000")),
            new SaleDto(null, 2, 1, LocalDate.now(), new BigDecimal("500"))
        );
        
        analyticsService.batchInsertSales(batch);
        
        List<CategoryRevenueDto> revenue = analyticsService.getRevenueByCategory();
        assertThat(revenue.get(0).totalRevenue()).isEqualByComparingTo(new BigDecimal("8000.00"));
    }

    @Test
    void testPlainSql() {
        List<ProductDto> results = analyticsService.findProductsByRawSql("Phone");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Phone");
    }

    @Test
    void testTransactionManagement() {
        analyticsService.updateProductPriceAndInsertAudit(1, new BigDecimal("1200"));
        
        List<ProductDto> products = analyticsService.findProductsByRawSql("Laptop");
        assertThat(products.get(0).price()).isEqualByComparingTo(new BigDecimal("1200"));
    }
    
    @Test
    void testTransactionRollback() {
        assertThrows(RuntimeException.class, () -> 
            analyticsService.updateProductPriceAndInsertAudit(999, new BigDecimal("1000"))
        );
    }

    @Test
    void testDelete() {
        int deleted = analyticsService.deleteSalesForProduct(5);
        assertThat(deleted).isGreaterThan(0);
    }
}
