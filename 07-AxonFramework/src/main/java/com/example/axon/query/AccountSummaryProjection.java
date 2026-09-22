package com.example.axon.query;

import com.example.axon.coreapi.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountSummaryProjection {

    private final AccountSummaryRepository repository;

    public AccountSummaryProjection(AccountSummaryRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event) {
        AccountSummary summary = new AccountSummary(event.accountId(), event.owner(), event.initialBalance());
        repository.save(summary);
    }

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        repository.findById(event.accountId()).ifPresent(summary -> {
            summary.setBalance(summary.getBalance().add(event.amount()));
        });
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        repository.findById(event.accountId()).ifPresent(summary -> {
            summary.setBalance(summary.getBalance().subtract(event.amount()));
        });
    }

    @EventHandler
    public void on(BankCardIssuedEvent event) {
        repository.findById(event.accountId()).ifPresent(summary -> {
            summary.setCardIssued(true);
        });
    }

    @QueryHandler
    @Transactional(readOnly = true)
    public AccountSummary handle(FindAccountSummaryQuery query) {
        return repository.findById(query.accountId()).orElse(null);
    }
}
