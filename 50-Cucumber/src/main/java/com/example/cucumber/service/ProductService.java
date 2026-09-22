package com.example.cucumber.service;

import com.example.cucumber.dto.ProductDto;
import com.example.cucumber.entity.Product;
import com.example.cucumber.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(ProductDto dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        Product product = new Product(null, dto.name(), dto.category(), dto.price());
        return productRepository.save(product);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
