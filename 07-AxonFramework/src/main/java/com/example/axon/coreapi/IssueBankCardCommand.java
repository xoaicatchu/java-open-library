package com.example.axon.coreapi;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
public record IssueBankCardCommand(@TargetAggregateIdentifier String accountId) {}
