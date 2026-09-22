package com.example.axon.controller;

import com.example.axon.coreapi.CreateAccountCommand;
import com.example.axon.coreapi.DepositMoneyCommand;
import com.example.axon.coreapi.FindAccountSummaryQuery;
import com.example.axon.coreapi.WithdrawMoneyCommand;
import com.example.axon.query.AccountSummary;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public AccountController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public CompletableFuture<String> createAccount(@RequestBody AccountRequest request) {
        String accountId = UUID.randomUUID().toString();
        return commandGateway.send(new CreateAccountCommand(accountId, request.owner(), request.initialBalance()));
    }

    @PostMapping("/{accountId}/deposit")
    public CompletableFuture<Void> deposit(@PathVariable String accountId, @RequestBody AmountRequest request) {
        return commandGateway.send(new DepositMoneyCommand(accountId, request.amount()));
    }

    @PostMapping("/{accountId}/withdraw")
    public CompletableFuture<Void> withdraw(@PathVariable String accountId, @RequestBody AmountRequest request) {
        return commandGateway.send(new WithdrawMoneyCommand(accountId, request.amount()));
    }

    @GetMapping("/{accountId}")
    public CompletableFuture<AccountSummary> getAccount(@PathVariable String accountId) {
        return queryGateway.query(
                new FindAccountSummaryQuery(accountId),
                ResponseTypes.instanceOf(AccountSummary.class)
        );
    }

    public record AccountRequest(String owner, BigDecimal initialBalance) {}
    public record AmountRequest(BigDecimal amount) {}
}
