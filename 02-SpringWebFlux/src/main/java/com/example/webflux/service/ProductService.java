package com.example.webflux.service;

import com.example.webflux.dto.ProductDto;
import com.example.webflux.entity.Product;
import com.example.webflux.exception.ProductNotFoundException;
import com.example.webflux.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<ProductDto> getAllProducts() {
        // Lấy tất cả sản phẩm
        return productRepository.findAll()
                .map(this::toDto);
    }

    public Mono<ProductDto> getProductById(Long id) {
        // Lấy sản phẩm theo id
        return productRepository.findById(id)
                .map(this::toDto)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + id)));
    }

    @Transactional
    public Mono<ProductDto> createProduct(ProductDto productDto) {
        // Tạo sản phẩm mới
        Product product = new Product(null, productDto.name(), productDto.price());
        return productRepository.save(product).map(this::toDto);
    }

    public Flux<ProductDto> getProductStream() {
        // Giả lập stream dữ liệu theo thời gian (Server-Sent Events)
        return productRepository.findAll()
                .delayElements(Duration.ofSeconds(1)) // Giả lập độ trễ
                .map(this::toDto);
    }

    public Flux<ProductDto> getProductsWithBackpressureDemo() {
        // Demo Backpressure: publisher phát dữ liệu liên tục, subscriber sẽ xử lý tùy theo request()
        return productRepository.findAll()
                .map(this::toDto)
                .log(); // Log để quan sát tín hiệu backpressure (request, onNext)
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }
}
