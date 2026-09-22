package com.example.caffeine;

import com.example.caffeine.dto.ProductDto;
import com.example.caffeine.service.ProductService;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CaffeineApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    private Long productId1;
    private Long productId2;

    @BeforeEach
    void setup() {
        ProductDto p1 = productService.createProduct(new ProductDto(null, "Laptop", new BigDecimal("1200")));
        ProductDto p2 = productService.createProduct(new ProductDto(null, "Phone", new BigDecimal("800")));
        productId1 = p1.id();
        productId2 = p2.id();
    }

    @Test
    void testSpringCacheableHitAndMiss() {
        // First call: Cache miss, hits DB
        ProductDto p1 = productService.getProductSpringCache(productId1);
        assertNotNull(p1);

        // Second call: Cache hit
        ProductDto p1Cached = productService.getProductSpringCache(productId1);
        assertEquals(p1.name(), p1Cached.name());

        // Verify it's in the cache manager
        assertNotNull(cacheManager.getCache("products").get(productId1));
    }

    @Test
    void testSpringCacheEviction() {
        productService.getProductSpringCache(productId1);
        assertNotNull(cacheManager.getCache("products").get(productId1));

        // Evict
        productService.evictSpringCache(productId1);
        
        // Cache should be empty for this key
        assertNull(cacheManager.getCache("products").get(productId1));
    }

    @Test
    void testManualCacheHitAndMiss() {
        // First call (miss)
        ProductDto p = productService.getProductManualCache(productId2);
        assertNotNull(p);

        // Second call (hit)
        ProductDto pCached = productService.getProductManualCache(productId2);
        assertEquals("Phone", pCached.name());
    }

    @Test
    void testManualCacheEviction() {
        productService.getProductManualCache(productId2);
        CacheStats initialStats = productService.getManualCacheStats();
        
        productService.evictManualCache(productId2);
        
        // Next call will be a miss
        productService.getProductManualCache(productId2);
        CacheStats updatedStats = productService.getManualCacheStats();
        
        assertTrue(updatedStats.missCount() > initialStats.missCount());
    }

    @Test
    void testLoadingCache() {
        ProductDto p = productService.getProductLoadingCache(productId1);
        assertNotNull(p);
        assertEquals("Laptop", p.name());
        
        // Hit
        ProductDto p2 = productService.getProductLoadingCache(productId1);
        assertEquals("Laptop", p2.name());
    }

    @Test
    void testAsyncLoadingCache() throws ExecutionException, InterruptedException {
        ProductDto p = productService.getProductAsyncCache(productId2).get();
        assertNotNull(p);
        assertEquals("Phone", p.name());
        
        // Hit
        ProductDto p2 = productService.getProductAsyncCache(productId2).get();
        assertEquals("Phone", p2.name());
    }

    @Test
    void testCacheStats() {
        // Clear manual cache eviction just to be safe
        productService.evictManualCache(productId1);
        
        // 1 Miss
        productService.getProductManualCache(productId1);
        
        // 2 Hits
        productService.getProductManualCache(productId1);
        productService.getProductManualCache(productId1);
        
        CacheStats stats = productService.getManualCacheStats();
        assertTrue(stats.hitCount() >= 2);
        assertTrue(stats.missCount() >= 1);
        assertTrue(stats.loadSuccessCount() >= 1);
    }
}
