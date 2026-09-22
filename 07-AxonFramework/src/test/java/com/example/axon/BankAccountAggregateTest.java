package com.example.axon;

import com.example.axon.command.BankAccountAggregate;
import com.example.axon.coreapi.*;
import com.example.axon.exception.InsufficientFundsException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class BankAccountAggregateTest {

    private FixtureConfiguration<BankAccountAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(BankAccountAggregate.class);
    }

    @Test
    void testCreateAccount() {
        fixture.givenNoPriorActivity()
                .when(new CreateAccountCommand("acc-1", "John Doe", new BigDecimal("100.00")))
                .expectEvents(new AccountCreatedEvent("acc-1", "John Doe", new BigDecimal("100.00")));
    }

    @Test
    void testDepositMoney() {
        fixture.given(new AccountCreatedEvent("acc-1", "John Doe", new BigDecimal("100.00")))
                .when(new DepositMoneyCommand("acc-1", new BigDecimal("50.00")))
                .expectEvents(new MoneyDepositedEvent("acc-1", new BigDecimal("50.00")));
    }

    @Test
    void testWithdrawMoney() {
        fixture.given(
                        new AccountCreatedEvent("acc-1", "John Doe", new BigDecimal("100.00")),
                        new MoneyDepositedEvent("acc-1", new BigDecimal("50.00"))
                )
                .when(new WithdrawMoneyCommand("acc-1", new BigDecimal("70.00")))
                .expectEvents(new MoneyWithdrawnEvent("acc-1", new BigDecimal("70.00")));
    }

    @Test
    void testWithdrawMoneyInsufficientFunds() {
        fixture.given(new AccountCreatedEvent("acc-1", "John Doe", new BigDecimal("100.00")))
                .when(new WithdrawMoneyCommand("acc-1", new BigDecimal("150.00")))
                .expectException(InsufficientFundsException.class);
    }

    @Test
    void testIssueBankCard() {
        fixture.given(new AccountCreatedEvent("acc-1", "John Doe", new BigDecimal("100.00")))
                .when(new IssueBankCardCommand("acc-1"))
                .expectEvents(new BankCardIssuedEvent("acc-1"));
    }
}
