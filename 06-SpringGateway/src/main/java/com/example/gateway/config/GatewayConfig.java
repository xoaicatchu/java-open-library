package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 1. Basic route with path predicate and AddRequestHeader/AddResponseHeader filter
                .route("basic_route", r -> r.path("/api/basic/**")
                        .filters(f -> f.addRequestHeader("X-Request-Source", "Gateway")
                                       .addResponseHeader("X-Handled-By", "Spring-Cloud-Gateway")
                                       .stripPrefix(2)) // Removes /api/basic
                        .uri("http://localhost:8106")) // routes back to self (downstream controller)
                
                // 2. RewritePath and query param predicate
                .route("rewrite_route", r -> r.path("/api/legacy/**")
                        .and().query("token", "123")
                        .filters(f -> f.rewritePath("/api/legacy/(?<segment>.*)", "/downstream/new/${segment}"))
                        .uri("http://localhost:8106"))
                
                // 3. Method predicate and Circuit Breaker integration
                .route("circuit_breaker_route", r -> r.path("/api/flaky/**")
                        .and().method("GET")
                        .filters(f -> f.stripPrefix(2)
                                       .circuitBreaker(config -> config
                                            .setName("myCircuitBreaker")
                                            .setFallbackUri("forward:/fallback")))
                        .uri("http://localhost:8106"))

                // 4. Header predicate and RequestRateLimiter (In-memory/Custom for demo)
                // Note: Spring Cloud Gateway provides Redis rate limiting out of the box. 
                // For a no-Redis demo, we will use a basic filter in Java DSL or omit it,
                // but since the requirement is "Rate Limiting with in-memory rate limiter", 
                // we'll implement a simple filter directly.
                .route("rate_limit_route", r -> r.path("/api/limited/**")
                        .and().header("X-RateLimit-App")
                        .filters(f -> f.stripPrefix(2)
                                       // A simple custom rate limiting filter logic
                                       .filter(new InMemoryRateLimiterFilter()))
                        .uri("http://localhost:8106"))
                .build();
    }
}
