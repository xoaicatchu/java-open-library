package com.example.springcache.service;

import com.example.springcache.dto.ProductDto;
import com.example.springcache.entity.Product;
import com.example.springcache.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Product product = new Product(null, dto.name(), dto.price(), dto.locale());
        product = productRepository.save(product);
        return mapToDto(product);
    }

    // Cacheable - save to cache
    // Custom key generation
    @Cacheable(value = "products", key = "#id + '_' + #locale", cacheManager = "caffeineCacheManager")
    public ProductDto getProductByIdAndLocale(Long id, String locale) {
        Product product = productRepository.findById(id).orElseThrow();
        return mapToDto(product);
    }

    // Conditional caching (unless = price > 1000)
    @Cacheable(value = "products", key = "#id", condition = "#id > 0", unless = "#result.price() > 1000")
    public ProductDto getProductByIdConditional(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return mapToDto(product);
    }
    
    // Different cache manager
    @Cacheable(value = "longProducts", key = "#id", cacheManager = "simpleCacheManager")
    public ProductDto getProductByIdLongCache(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return mapToDto(product);
    }

    // CachePut - update cache after save
    @Transactional
    @CachePut(value = "products", key = "#id")
    public ProductDto updateProductPrice(Long id, Double newPrice) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setPrice(newPrice);
        productRepository.save(product);
        return mapToDto(product);
    }

    // CacheEvict - clear specific key
    @CacheEvict(value = "products", key = "#id")
    public void evictProduct(Long id) {
        // Evicts product from cache
    }

    // Caching - multiple annotations
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "products", key = "#id", cacheManager = "caffeineCacheManager"),
                    @CacheEvict(value = "longProducts", key = "#id", cacheManager = "simpleCacheManager")
            }
    )
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void evictAllProducts() {
        // Clear all entries
    }

    private ProductDto mapToDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice(), product.getLocale());
    }
}
