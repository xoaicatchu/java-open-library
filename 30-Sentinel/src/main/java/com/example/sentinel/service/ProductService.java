package com.example.sentinel.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.sentinel.dto.ProductDto;
import com.example.sentinel.entity.Product;
import com.example.sentinel.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Flow control: Giới hạn số lượng request
     * Nếu bị chặn, sẽ gọi phương thức blockHandler (phải có cùng signature + BlockException)
     */
    @SentinelResource(value = "getProduct", blockHandler = "handleGetProductBlock")
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    // Phương thức xử lý khi bị block (Flow limit)
    public Product handleGetProductBlock(Long id, BlockException ex) {
        Product fallbackProduct = new Product("Blocked Product (Flow limit)", 0.0);
        fallbackProduct.setId(id);
        return fallbackProduct;
    }

    /**
     * Degrade control (Circuit Breaker): Ngắt mạch nếu tỷ lệ lỗi cao
     * Nếu bị lỗi (Exception) quá tỷ lệ cho phép, sẽ bị ngắt mạch và gọi fallback
     */
    @Transactional
    @SentinelResource(value = "createProduct", fallback = "handleCreateProductFallback")
    public Product createProduct(ProductDto dto) {
        // Cố tình gây lỗi để test Circuit Breaker nếu tên sản phẩm là "error"
        if ("error".equalsIgnoreCase(dto.name())) {
            throw new RuntimeException("Simulated error for degradation test");
        }
        
        Product product = new Product(dto.name(), dto.price());
        return productRepository.save(product);
    }

    // Phương thức fallback khi gặp lỗi hoặc bị ngắt mạch
    public Product handleCreateProductFallback(ProductDto dto, Throwable t) {
        Product fallbackProduct = new Product("Fallback Product", 0.0);
        fallbackProduct.setId(-1L);
        return fallbackProduct;
    }

    /**
     * Dùng API lập trình (Programmatic) thay vì Annotation
     */
    @Transactional
    public boolean deleteProduct(Long id) {
        try (Entry entry = SphU.entry("deleteProduct")) {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (BlockException ex) {
            // Xử lý khi bị block
            return false;
        }
    }
}
