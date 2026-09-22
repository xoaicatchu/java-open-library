package com.example.flyway.controller;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flyway")
public class FlywayController {

    private final Flyway flyway;

    public FlywayController(Flyway flyway) {
        this.flyway = flyway;
    }

    @GetMapping("/history")
    public List<MigrationInfoResponse> getHistory() {
        return Arrays.stream(flyway.info().applied())
                .map(info -> new MigrationInfoResponse(
                        info.getVersion() != null ? info.getVersion().toString() : "Repeatable",
                        info.getDescription(),
                        info.getType().name(),
                        info.getState().name()
                ))
                .collect(Collectors.toList());
    }

    public record MigrationInfoResponse(String version, String description, String type, String state) {}
}
