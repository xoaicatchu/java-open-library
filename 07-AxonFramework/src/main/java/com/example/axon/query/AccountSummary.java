package com.example.axon.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class AccountSummary {

    @Id
    private String accountId;
    private String owner;
    private BigDecimal balance;
    private boolean cardIssued;

    public AccountSummary() {
    }

    public AccountSummary(String accountId, String owner, BigDecimal balance) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.cardIssued = false;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isCardIssued() {
        return cardIssued;
    }

    public void setCardIssued(boolean cardIssued) {
        this.cardIssued = cardIssued;
    }
}
