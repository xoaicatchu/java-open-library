package com.example.restclient.config;

import com.example.restclient.service.ProductHttpExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${server.port:8135}")
    private int port;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("X-Custom-Header", "SpringRestClientDemo");
            return execution.execute(request, body);
        };

        return builder
                .baseUrl("http://localhost:" + port)
                .requestInterceptor(interceptor)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public ProductHttpExchange productHttpExchange(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ProductHttpExchange.class);
    }
}
