package com.example.springcache;

import com.example.springcache.dto.ProductDto;
import com.example.springcache.entity.Product;
import com.example.springcache.repository.ProductRepository;
import com.example.springcache.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceCacheTest {

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("caffeineCacheManager")
    private CacheManager caffeineCacheManager;

    @Autowired
    @Qualifier("simpleCacheManager")
    private CacheManager simpleCacheManager;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        caffeineCacheManager.getCache("products").clear();
        simpleCacheManager.getCache("longProducts").clear();
    }

    @Test
    void testCacheableCustomKey() {
        Product mockProduct = new Product(1L, "Test Product", 500.0, "en-US");
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // First call - invokes repository
        ProductDto result1 = productService.getProductByIdAndLocale(1L, "en-US");
        assertNotNull(result1);
        verify(productRepository, times(1)).findById(1L);

        // Second call - from cache
        ProductDto result2 = productService.getProductByIdAndLocale(1L, "en-US");
        assertEquals(result1, result2);
        verify(productRepository, times(1)).findById(1L); // Still 1
        
        Cache cache = caffeineCacheManager.getCache("products");
        assertNotNull(cache.get("1_en-US"));
    }

    @Test
    void testCacheConditionalUnless() {
        // Price > 1000 so it won't be cached based on "unless"
        Product expensiveProduct = new Product(2L, "Expensive", 1500.0, "en-US");
        when(productRepository.findById(2L)).thenReturn(Optional.of(expensiveProduct));

        productService.getProductByIdConditional(2L);
        productService.getProductByIdConditional(2L);

        // Invoked twice because it was not cached
        verify(productRepository, times(2)).findById(2L);

        // Conditional condition = id > 0
        Product normalProduct = new Product(3L, "Normal", 500.0, "en-US");
        when(productRepository.findById(3L)).thenReturn(Optional.of(normalProduct));

        productService.getProductByIdConditional(3L);
        productService.getProductByIdConditional(3L);
        
        // Invoked once because it was cached
        verify(productRepository, times(1)).findById(3L);
    }

    @Test
    void testCachePut() {
        Product mockProduct = new Product(4L, "Update me", 100.0, "en-US");
        when(productRepository.findById(4L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductDto updated = productService.updateProductPrice(4L, 200.0);
        assertEquals(200.0, updated.price());

        Cache cache = caffeineCacheManager.getCache("products");
        assertNotNull(cache.get(4L));
        ProductDto cached = (ProductDto) cache.get(4L).get();
        assertEquals(200.0, cached.price());
    }

    @Test
    void testCacheEvict() {
        // Put something in cache
        Cache cache = caffeineCacheManager.getCache("products");
        cache.put(5L, new ProductDto(5L, "To Evict", 10.0, "vi-VN"));

        assertNotNull(cache.get(5L));

        // Evict
        productService.evictProduct(5L);

        assertNull(cache.get(5L));
    }

    @Test
    void testCachingMultipleAnnotations() {
        Cache productsCache = caffeineCacheManager.getCache("products");
        Cache longProductsCache = simpleCacheManager.getCache("longProducts");

        productsCache.put(6L, new ProductDto(6L, "Multi", 20.0, "en"));
        longProductsCache.put(6L, new ProductDto(6L, "Multi", 20.0, "en"));

        assertNotNull(productsCache.get(6L));
        assertNotNull(longProductsCache.get(6L));

        productService.deleteProduct(6L);

        assertNull(productsCache.get(6L));
        assertNull(longProductsCache.get(6L));
        verify(productRepository, times(1)).deleteById(6L);
    }

    @Test
    void testLongProductsCacheManager() {
        Product mockProduct = new Product(7L, "Long Cache", 50.0, "en-US");
        when(productRepository.findById(7L)).thenReturn(Optional.of(mockProduct));

        productService.getProductByIdLongCache(7L);
        productService.getProductByIdLongCache(7L);

        verify(productRepository, times(1)).findById(7L);

        Cache cache = simpleCacheManager.getCache("longProducts");
        assertNotNull(cache.get(7L));
    }
}
