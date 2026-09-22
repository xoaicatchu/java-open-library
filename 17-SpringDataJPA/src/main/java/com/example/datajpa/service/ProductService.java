package com.example.datajpa.service;

import com.example.datajpa.dto.CreateProductRequest;
import com.example.datajpa.dto.ProductResponse;
import com.example.datajpa.entity.Category;
import com.example.datajpa.entity.Product;
import com.example.datajpa.exception.ResourceNotFoundException;
import com.example.datajpa.projection.ProductSummaryDto;
import com.example.datajpa.repository.CategoryRepository;
import com.example.datajpa.repository.ProductRepository;
import com.example.datajpa.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToResponse(product);
    }

    public List<ProductResponse> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasName(name))
                .and(ProductSpecification.priceGreaterThan(minPrice))
                .and(ProductSpecification.priceLessThan(maxPrice));
        
        return productRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductSummaryDto> getProductSummaries(String name) {
        if (name == null || name.isBlank()) {
            return productRepository.findDtoByNameContaining("");
        }
        return productRepository.findDtoByNameContaining(name);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }
        
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        
        if (category != null) {
            category.addProduct(product);
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), categoryName);
    }
}
