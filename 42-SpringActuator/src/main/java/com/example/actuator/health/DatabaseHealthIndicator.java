package com.example.actuator.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Mô phỏng kiểm tra sức khỏe của database
        boolean isDatabaseUp = checkDatabaseConnection();
        if (isDatabaseUp) {
            return Health.up().withDetail("database", "Service is running").build();
        }
        return Health.down().withDetail("database", "Service is down").build();
    }

    private boolean checkDatabaseConnection() {
        // Logic thực tế sẽ nằm ở đây
        return true;
    }
}
