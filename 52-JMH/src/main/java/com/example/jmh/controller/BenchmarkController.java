package com.example.jmh.controller;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/benchmarks")
public class BenchmarkController {

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return Map.of(
            "message", "JMH Microbenchmark Harness",
            "availableBenchmarks", new String[]{
                "StringBenchmark (+ vs StringBuilder vs StringJoiner)",
                "CollectionBenchmark (ArrayList vs LinkedList)",
                "MapBenchmark (HashMap vs TreeMap vs ConcurrentHashMap)",
                "SortBenchmark (Arrays.sort vs parallelSort)"
            },
            "hint", "POST /api/benchmarks/run-quick to execute a minimal 1-iteration smoke benchmark"
        );
    }

    @PostMapping("/run-quick")
    public Map<String, String> runQuickBenchmark() {
        try {
            Options opt = new OptionsBuilder()
                .include("StringBenchmark")
                .forks(0)
                .warmupIterations(0)
                .measurementIterations(1)
                .build();
            new Runner(opt).run();
            return Map.of("status", "SUCCESS", "message", "JMH benchmark completed. Check console logs for detailed metrics.");
        } catch (Exception e) {
            return Map.of("status", "ERROR", "message", e.getMessage());
        }
    }
}
