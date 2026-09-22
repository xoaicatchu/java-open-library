package com.example.micrometer.config;

import com.example.micrometer.service.OrderService;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    // Kích hoạt annotation @Timed
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    // Custom MeterBinder để theo dõi thông số nghiệp vụ (business metrics)
    @Bean
    public MeterBinder orderMetricsBinder(OrderService orderService) {
        return registry -> {
            // Gauge: Theo dõi kích thước hàng đợi xử lý (giả lập)
            registry.gauge("orders.processing.queue", orderService, OrderService::getQueueSize);
            
            // Gauge: Theo dõi số lượng active users (giả lập)
            registry.gauge("users.active.count", orderService, OrderService::getActiveUsersCount);
        };
    }
}
