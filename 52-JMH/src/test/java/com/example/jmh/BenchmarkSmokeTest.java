package com.example.jmh;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BenchmarkSmokeTest {

    @Test
    void verifyStringBenchmarkCompiles() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(com.example.jmh.benchmark.StringBenchmark.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(0)
                .build();
        
        // This confirms JMH can process the class and it compiles
        new Runner(opt).run();
    }
    
    @Test
    void verifyCollectionBenchmarkCompiles() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(com.example.jmh.benchmark.CollectionBenchmark.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(0)
                .build();
                
        new Runner(opt).run();
    }

    @Test
    void verifyMapBenchmarkCompiles() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(com.example.jmh.benchmark.MapBenchmark.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(0)
                .build();
                
        new Runner(opt).run();
    }
    
    @Test
    void verifySortBenchmarkCompiles() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(com.example.jmh.benchmark.SortBenchmark.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(0)
                .build();
                
        new Runner(opt).run();
    }
}
