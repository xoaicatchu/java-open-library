package com.example.axon;

import com.example.axon.controller.AccountController;
import com.example.axon.coreapi.CreateAccountCommand;
import com.example.axon.coreapi.FindAccountSummaryQuery;
import com.example.axon.query.AccountSummary;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommandGateway commandGateway;

    @MockBean
    private QueryGateway queryGateway;

    @Test
    void testCreateAccountEndpoint() throws Exception {
        when(commandGateway.send(any(CreateAccountCommand.class)))
                .thenReturn(CompletableFuture.completedFuture("acc-123"));

        MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "owner": "Alice",
                                  "initialBalance": 1000
                                }
                                """))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAccountEndpoint() throws Exception {
        AccountSummary summary = new AccountSummary("acc-123", "Alice", new BigDecimal("1000.00"));
        when(queryGateway.query(any(FindAccountSummaryQuery.class), any(org.axonframework.messaging.responsetypes.ResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(summary));

        MvcResult result = mockMvc.perform(get("/accounts/acc-123"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner").value("Alice"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }
}
