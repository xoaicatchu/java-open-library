package com.example.events.event;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class GenericDomainEvent<T> implements ResolvableTypeProvider {
    private final T payload;
    private final String action;

    public GenericDomainEvent(T payload, String action) {
        this.payload = payload;
        this.action = action;
    }

    public T getPayload() {
        return payload;
    }

    public String getAction() {
        return action;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(this.payload));
    }
}
