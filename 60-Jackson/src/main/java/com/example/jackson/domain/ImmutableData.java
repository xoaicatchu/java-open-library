package com.example.jackson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImmutableData {
    private final String name;
    private final int value;

    @JsonCreator
    public ImmutableData(@JsonProperty("name") String name, @JsonProperty("value") int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public int getValue() {
        return value;
    }
}
