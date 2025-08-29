package com.example.fabrick_demo.controller;

import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import com.example.fabrick_demo.exception.GlobalExceptionHandler;
import com.example.fabrick_demo.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({GlobalExceptionHandler.class, AccountControllerWebTest.Config.class})
class AccountControllerWebTest {

    @TestConfiguration
    static class Config {
        @Bean AccountService accountService() {
            return mock(AccountService.class);
        }
    }

    @Autowired MockMvc mvc;
    @Autowired AccountService service;
    @Autowired ObjectMapper om;

    @Test
    void balance_ok() throws Exception {
        var payload = new BalanceResponse.Payload();
        payload.setCurrency("EUR");
        var resp = new BalanceResponse();
        resp.setStatus("OK"); resp.setPayload(payload);

        when(service.getBalance(14537780L)).thenReturn(resp);

        mvc.perform(get("/api/gbs/banking/v4.0/accounts/{id}/balance", 14537780L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.payload.currency").value("EUR"));

        verify(service).getBalance(14537780L);
    }

    @Test
    void transactions_ok() throws Exception {
        var tr = new TransactionsResponse();
        tr.setStatus("OK");
        when(service.getTransactions(14537780L, "2025-08-01", "2025-08-27")).thenReturn(tr);

        mvc.perform(get("/api/gbs/banking/v4.0/accounts/{id}/transactions", 14537780L)
                        .param("fromAccountingDate", "2025-08-01")
                        .param("toAccountingDate", "2025-08-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        verify(service).getTransactions(14537780L, "2025-08-01", "2025-08-27");
    }

    @Test
    void transactions_bad_date_400() throws Exception {
        mvc.perform(get("/api/gbs/banking/v4.0/accounts/{id}/transactions", 14537780L)
                        .param("fromAccountingDate", "2025-8-27")
                        .param("toAccountingDate", "2025-08-27"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("KO"))
                .andExpect(jsonPath("$.errors[0].message").exists());

        verifyNoInteractions(service); // la validazione fallisce prima del service
    }

    @Test
    void money_transfers_ok() throws Exception {
        var req = new MoneyTransferRequest();
        var creditor = new MoneyTransferRequest.Creditor();
        var account = new MoneyTransferRequest.Account();
        account.setAccountCode("IT23A0336844430152923804660");
        creditor.setName("John Doe");
        creditor.setAccount(account);
        req.setCreditor(creditor);
        req.setDescription("Payment invoice 75/2017");
        req.setCurrency("EUR");
        req.setAmount(new BigDecimal("800"));
        req.setExecutionDate(LocalDate.now());

        var resp = new MoneyTransferResponse();
        resp.setCode("OK");
        resp.setDescription("Transfer accepted");

        when(service.createTransfer(eq(14537780L), any(MoneyTransferRequest.class))).thenReturn(resp);

        mvc.perform(post("/api/gbs/banking/v4.0/accounts/{id}/payments/money-transfers", 14537780L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.description").value("Transfer accepted"));

        verify(service).createTransfer(eq(14537780L), any(MoneyTransferRequest.class));
    }

    @Test
    void money_transfers_upstream400_propagato() throws Exception {
        String upstream = """
      {"status":"KO","errors":[{"code":"SCT008","description":"La data ordine non puo' essere successiva alla data corrente"}],"payload":{}}
    """;

        var feignReq = Request.create(Request.HttpMethod.POST, "/",
                Map.of("Content-Type", List.of("application/json")),
                upstream.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8, new RequestTemplate());
        var ex = new FeignException.BadRequest("bad", feignReq, upstream.getBytes(StandardCharsets.UTF_8), Map.of());

        when(service.createTransfer(eq(14537780L), any(MoneyTransferRequest.class))).thenThrow(ex);

        var minimalBody = """
      {"creditor":{"name":"John","account":{"accountCode":"IT23A0336844430152923804660"}},"description":"x","currency":"EUR","amount":10,"executionDate":"2025-08-27"}
    """;

        mvc.perform(post("/api/gbs/banking/v4.0/accounts/{id}/payments/money-transfers", 14537780L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(upstream));
    }
}