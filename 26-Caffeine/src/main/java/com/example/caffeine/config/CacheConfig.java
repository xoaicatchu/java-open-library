package com.example.caffeine.config;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.example.caffeine.entity.Product;
import com.example.caffeine.repository.ProductRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;

@Configuration
public class CacheConfig {

    // Spring Cache integration
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("products");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(100)
                .recordStats());
        return cacheManager;
    }

    // 1. Manual Cache
    @Bean
    public Cache<Long, Product> manualProductCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES) // Time-based eviction
                .maximumSize(50) // Size-based eviction
                .recordStats() // Enable statistics
                .build();
    }

    // 2. LoadingCache
    @Bean
    public LoadingCache<Long, Product> loadingProductCache(ProductRepository repository) {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .recordStats()
                .build(key -> repository.findById(key).orElse(null)); // CacheLoader
    }

    // 3. AsyncLoadingCache
    @Bean
    public AsyncLoadingCache<Long, Product> asyncLoadingProductCache(ProductRepository repository) {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .recordStats()
                .buildAsync((key, executor) -> 
                    CompletableFuture.supplyAsync(() -> repository.findById(key).orElse(null), executor)
                );
    }
}
