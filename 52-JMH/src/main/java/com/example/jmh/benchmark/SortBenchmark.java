package com.example.jmh.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
public class SortBenchmark {

    @Param({"10000", "100000"})
    private int size;

    private int[] array;

    @Setup(Level.Invocation)
    public void setup() {
        array = new int[size];
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }
    }

    @Benchmark
    public void sequentialSort(Blackhole blackhole) {
        Arrays.sort(array);
        blackhole.consume(array);
    }

    @Benchmark
    public void parallelSort(Blackhole blackhole) {
        Arrays.parallelSort(array);
        blackhole.consume(array);
    }
}
