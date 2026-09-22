package com.example.webflux.config;

import com.example.webflux.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    // 2. Functional Endpoints
    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route()
                .GET("/functional/products", handler::getAllProducts)
                .GET("/functional/products/{id}", handler::getProductById)
                .build();
    }
}
