package com.example.openapigen.service;

import com.example.openapigen.model.CreateProductRequest;
import com.example.openapigen.model.Product;
import com.example.openapigen.model.UpdateProductRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product createProduct(CreateProductRequest request) {
        long id = idCounter.getAndIncrement();
        Product product = new Product();
        product.setId(id);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        products.put(id, product);
        return product;
    }

    public Optional<Product> updateProduct(Long id, UpdateProductRequest request) {
        if (!products.containsKey(id)) {
            return Optional.empty();
        }
        Product product = products.get(id);
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        return Optional.of(product);
    }

    public boolean deleteProduct(Long id) {
        return products.remove(id) != null;
    }
}
