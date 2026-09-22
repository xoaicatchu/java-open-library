package com.example.axon.command;

import com.example.axon.coreapi.*;
import com.example.axon.exception.InsufficientFundsException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
public class BankAccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private BigDecimal balance;
    private String owner;
    private boolean cardIssued;

    // Required by Axon
    protected BankAccountAggregate() {
    }

    @CommandHandler
    public BankAccountAggregate(CreateAccountCommand command) {
        if (command.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        AggregateLifecycle.apply(new AccountCreatedEvent(command.accountId(), command.owner(), command.initialBalance()));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.accountId();
        this.owner = event.owner();
        this.balance = event.initialBalance();
        this.cardIssued = false;
    }

    @CommandHandler
    public void handle(DepositMoneyCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        AggregateLifecycle.apply(new MoneyDepositedEvent(command.accountId(), command.amount()));
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
    }

    @CommandHandler
    public void handle(WithdrawMoneyCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (this.balance.subtract(command.amount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds for account " + this.accountId);
        }
        AggregateLifecycle.apply(new MoneyWithdrawnEvent(command.accountId(), command.amount()));
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = this.balance.subtract(event.amount());
    }

    @CommandHandler
    public void handle(IssueBankCardCommand command) {
        if (this.cardIssued) {
            throw new IllegalStateException("Card already issued");
        }
        AggregateLifecycle.apply(new BankCardIssuedEvent(command.accountId()));
    }

    @EventSourcingHandler
    public void on(BankCardIssuedEvent event) {
        this.cardIssued = true;
    }
}
