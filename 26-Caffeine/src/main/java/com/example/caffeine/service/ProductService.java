package com.example.caffeine.service;

import com.example.caffeine.dto.ProductDto;
import com.example.caffeine.entity.Product;
import com.example.caffeine.repository.ProductRepository;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;
    private final Cache<Long, Product> manualCache;
    private final LoadingCache<Long, Product> loadingCache;
    private final AsyncLoadingCache<Long, Product> asyncLoadingCache;

    public ProductService(ProductRepository repository,
                          @Qualifier("manualProductCache") Cache<Long, Product> manualCache,
                          @Qualifier("loadingProductCache") LoadingCache<Long, Product> loadingCache,
                          @Qualifier("asyncLoadingProductCache") AsyncLoadingCache<Long, Product> asyncLoadingCache) {
        this.repository = repository;
        this.manualCache = manualCache;
        this.loadingCache = loadingCache;
        this.asyncLoadingCache = asyncLoadingCache;
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Product product = new Product(dto.name(), dto.price());
        product = repository.save(product);
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    // 6. Spring Cache integration
    @Cacheable(value = "products", key = "#id")
    public ProductDto getProductSpringCache(Long id) {
        return repository.findById(id)
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice()))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    // 1. Manual Cache usage
    public ProductDto getProductManualCache(Long id) {
        Product product = manualCache.get(id, k -> repository.findById(k).orElse(null));
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    // 2. LoadingCache usage
    public ProductDto getProductLoadingCache(Long id) {
        Product product = loadingCache.get(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    // 3. AsyncLoadingCache usage
    public CompletableFuture<ProductDto> getProductAsyncCache(Long id) {
        return asyncLoadingCache.get(id).thenApply(product -> {
            if (product == null) {
                throw new IllegalArgumentException("Product not found");
            }
            return new ProductDto(product.getId(), product.getName(), product.getPrice());
        });
    }

    // Cache management & Eviction
    @CacheEvict(value = "products", key = "#id")
    public void evictSpringCache(Long id) {
        // Evicts from Spring Cache
    }

    public void evictManualCache(Long id) {
        manualCache.invalidate(id);
    }

    // 5. Statistics
    public CacheStats getManualCacheStats() {
        return manualCache.stats();
    }
}
