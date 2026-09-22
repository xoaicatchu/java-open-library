package com.example.webmvc.service;

import com.example.webmvc.dto.CreateProductRequest;
import com.example.webmvc.dto.ProductResponse;
import com.example.webmvc.dto.UpdateProductRequest;
import com.example.webmvc.entity.Product;
import com.example.webmvc.exception.ResourceNotFoundException;
import com.example.webmvc.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(ProductResponse::fromEntity);
    }

    public ProductResponse findById(Long id) {
        return repository.findById(id)
                .map(ProductResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public List<ProductResponse> findByCategory(String category) {
        return repository.findByCategoryIgnoreCase(category).stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    public Page<ProductResponse> search(String keyword, Pageable pageable) {
        return repository.searchByName(keyword, pageable).map(ProductResponse::fromEntity);
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        var product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.stockQuantity()
        );
        return ProductResponse.fromEntity(repository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setStockQuantity(request.stockQuantity());
        product.setActive(request.active());

        return ProductResponse.fromEntity(repository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        repository.deleteById(id);
    }
}
