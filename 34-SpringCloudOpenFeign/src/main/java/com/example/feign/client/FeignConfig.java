package com.example.feign.client;

import com.example.feign.exception.ResourceNotFoundException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Thêm auth header
                template.header("Authorization", "Bearer my-secret-token");
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 404) {
                return new ResourceNotFoundException("Resource not found: " + methodKey);
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
