package com.example.axon.saga;

import com.example.axon.coreapi.AccountCreatedEvent;
import com.example.axon.coreapi.BankCardIssuedEvent;
import com.example.axon.coreapi.IssueBankCardCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class AccountManagementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "accountId")
    public void on(AccountCreatedEvent event) {
        // Automatically issue a bank card when account is created
        commandGateway.send(new IssueBankCardCommand(event.accountId()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "accountId")
    public void on(BankCardIssuedEvent event) {
        // Saga ends when the card is successfully issued
    }
}
