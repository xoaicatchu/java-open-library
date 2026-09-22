package com.example.webflux.handler;

import com.example.webflux.dto.ProductDto;
import com.example.webflux.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

    private final ProductService productService;

    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        // Xử lý request lấy danh sách product qua Functional Endpoint
        return ServerResponse.ok().body(productService.getAllProducts(), ProductDto.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return productService.getProductById(id)
                .flatMap(productDto -> ServerResponse.ok().bodyValue(productDto))
                // Error handling sẽ được thực hiện bởi GlobalExceptionHandler nếu lỗi throw trong Mono,
                // hoặc ta xử lý trực tiếp ở đây.
                .onErrorResume(e -> ServerResponse.notFound().build());
    }
}
