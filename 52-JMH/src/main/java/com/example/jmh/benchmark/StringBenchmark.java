package com.example.jmh.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringBenchmark {

    @Param({"10", "100"})
    private int iterations;

    private String baseString;

    @Setup(Level.Iteration)
    public void setup() {
        baseString = "test";
    }

    @Benchmark
    public void testPlus(Blackhole blackhole) {
        String result = "";
        for (int i = 0; i < iterations; i++) {
            result += baseString;
        }
        blackhole.consume(result);
    }

    @Benchmark
    public void testStringBuilder(Blackhole blackhole) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append(baseString);
        }
        blackhole.consume(sb.toString());
    }

    @Benchmark
    public void testStringFormat(Blackhole blackhole) {
        String result = "";
        for (int i = 0; i < iterations; i++) {
            result = String.format("%s%s", result, baseString);
        }
        blackhole.consume(result);
    }
    
    @Benchmark
    public void testStringJoiner(Blackhole blackhole) {
        StringJoiner sj = new StringJoiner("");
        for (int i = 0; i < iterations; i++) {
            sj.add(baseString);
        }
        blackhole.consume(sj.toString());
    }
}
