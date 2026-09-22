package com.example.axon;

import com.example.axon.coreapi.AccountCreatedEvent;
import com.example.axon.coreapi.FindAccountSummaryQuery;
import com.example.axon.coreapi.MoneyDepositedEvent;
import com.example.axon.query.AccountSummary;
import com.example.axon.query.AccountSummaryProjection;
import com.example.axon.query.AccountSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountSummaryProjectionTest {

    @Mock
    private AccountSummaryRepository repository;

    @InjectMocks
    private AccountSummaryProjection projection;

    @Test
    void testOnAccountCreatedEvent() {
        AccountCreatedEvent event = new AccountCreatedEvent("acc-1", "Jane", new BigDecimal("200.00"));
        projection.on(event);
        verify(repository).save(any(AccountSummary.class));
    }

    @Test
    void testOnMoneyDepositedEvent() {
        AccountSummary summary = new AccountSummary("acc-1", "Jane", new BigDecimal("200.00"));
        when(repository.findById("acc-1")).thenReturn(Optional.of(summary));
        
        MoneyDepositedEvent event = new MoneyDepositedEvent("acc-1", new BigDecimal("50.00"));
        projection.on(event);
        
        assertEquals(new BigDecimal("250.00"), summary.getBalance());
    }

    @Test
    void testFindAccountSummaryQuery() {
        AccountSummary summary = new AccountSummary("acc-1", "Jane", new BigDecimal("200.00"));
        when(repository.findById("acc-1")).thenReturn(Optional.of(summary));
        
        FindAccountSummaryQuery query = new FindAccountSummaryQuery("acc-1");
        AccountSummary result = projection.handle(query);
        
        assertEquals("Jane", result.getOwner());
        assertEquals(new BigDecimal("200.00"), result.getBalance());
    }
}
