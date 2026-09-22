package com.example.aop.service;

import com.example.aop.annotation.Auditable;
import com.example.aop.annotation.RateLimit;
import com.example.aop.dto.ProductDto;
import com.example.aop.entity.Product;
import com.example.aop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice()))
                .collect(Collectors.toList());
    }

    @Auditable(action = "CREATE_PRODUCT")
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Product p = new Product(dto.name(), dto.price());
        p = productRepository.save(p);
        return new ProductDto(p.getId(), p.getName(), p.getPrice());
    }
    
    @Auditable(action = "DELETE_PRODUCT")
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    // Phương thức này sẽ bị lỗi để test @AfterThrowing
    public ProductDto getProductWithError(Long id) {
        throw new IllegalArgumentException("Product not found with id: " + id);
    }

    @RateLimit(maxRequests = 3, timeWindowMs = 10000)
    public String getSensitiveData() {
        return "Sensitive Data";
    }
}
