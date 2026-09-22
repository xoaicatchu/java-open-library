package com.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    void contextLoads() {
    }

    @Test
    void basicRoute_addsHeadersAndStripsPrefix() {
        webClient.get().uri("/api/basic/downstream/hello")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Handled-By", "Spring-Cloud-Gateway")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Hello from downstream!")
                .jsonPath("$.source").isEqualTo("Gateway"); // Thêm bởi AddRequestHeader
    }

    @Test
    void rewriteRoute_modifiesPath() {
        // Query param "token=123" is required by route predicate
        webClient.get().uri("/api/legacy/info?token=123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("This is the rewritten path!");
    }

    @Test
    void rewriteRoute_failsWithoutQueryParam() {
        webClient.get().uri("/api/legacy/info")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void circuitBreakerRoute_fallbackWhenDownstreamFails() {
        // Downstream returns 500, CB should redirect to fallback
        webClient.get().uri("/api/flaky/downstream/flaky/status")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Fallback executed")
                .jsonPath("$.detail").isEqualTo("Service is currently unavailable. Please try again later.");
    }

    @Test
    void rateLimitRoute_blocksAfterLimitExceeded() {
        // Limit is 5 in memory
        for (int i = 0; i < 5; i++) {
            webClient.get().uri("/api/limited/downstream/limited/data")
                    .header("X-RateLimit-App", "TestApp")
                    .exchange()
                    .expectStatus().isOk();
        }

        // The 6th request should be blocked
        webClient.get().uri("/api/limited/downstream/limited/data")
                .header("X-RateLimit-App", "TestApp")
                .exchange()
                .expectStatus().isEqualTo(429); // TOO_MANY_REQUESTS
    }
}
