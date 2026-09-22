package com.example.jmh.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
public class MapBenchmark {

    @Param({"1000"})
    private int size;

    private Map<String, Integer> hashMap;
    private Map<String, Integer> treeMap;
    private Map<String, Integer> concurrentHashMap;
    private String[] keys;

    @Setup(Level.Trial)
    public void setup() {
        hashMap = new HashMap<>();
        treeMap = new TreeMap<>();
        concurrentHashMap = new ConcurrentHashMap<>();
        keys = new String[size];
        
        for (int i = 0; i < size; i++) {
            String key = "key" + i;
            keys[i] = key;
            hashMap.put(key, i);
            treeMap.put(key, i);
            concurrentHashMap.put(key, i);
        }
    }

    @Benchmark
    public void getHashMap(Blackhole blackhole) {
        for (String key : keys) {
            blackhole.consume(hashMap.get(key));
        }
    }

    @Benchmark
    public void getTreeMap(Blackhole blackhole) {
        for (String key : keys) {
            blackhole.consume(treeMap.get(key));
        }
    }

    @Benchmark
    public void getConcurrentHashMap(Blackhole blackhole) {
        for (String key : keys) {
            blackhole.consume(concurrentHashMap.get(key));
        }
    }
}
