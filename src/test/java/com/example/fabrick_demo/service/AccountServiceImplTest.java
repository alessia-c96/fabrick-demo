package com.example.fabrick_demo.service;

import com.example.fabrick_demo.client.FabrickClient;
import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    FabrickClient client;
    @InjectMocks
    AccountServiceImpl service;

    @Test
    void getBalance_test() {
        long id = 14537780L;
        var payload = new BalanceResponse.Payload();
        payload.setCurrency("EUR");
        var expected = new BalanceResponse();
        expected.setStatus("OK");
        expected.setPayload(payload);

        when(client.getBalance(id)).thenReturn(expected);

        var actual = service.getBalance(id);

        assertThat(actual).isSameAs(expected);
        verify(client).getBalance(id);
        verifyNoMoreInteractions(client);
    }

    @Test
    void getTransactions_test() {
        long id = 14537780L;
        String from = "2025-08-01";
        String to = "2025-08-27";
        var expected = new TransactionsResponse();
        expected.setStatus("OK");

        when(client.getTransactions(id, from, to)).thenReturn(expected);

        var actual = service.getTransactions(id, from, to);

        assertThat(actual).isSameAs(expected);
        verify(client).getTransactions(id, from, to);
        verifyNoMoreInteractions(client);
    }

    @Test
    void createTransfer_test() {
        long id = 14537780L;

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

        var expected = new MoneyTransferResponse();
        expected.setCode("OK");
        expected.setDescription("Transfer accepted");

        when(client.createTransfer(id, req)).thenReturn(expected);

        var actual = service.createTransfer(id, req);

        assertThat(actual).isSameAs(expected);
        verify(client).createTransfer(id, req);
        verifyNoMoreInteractions(client);
    }

    @Test
    void getBalance_Exception() {
        long id = 14537780L;
        String upstream = """
            {"status":"KO","errors":[{"code":"GEN001","description":"Errore generico"}]}
            """;
        var feignReq = Request.create(
                Request.HttpMethod.GET, "/",
                Map.of("Accept", List.of("application/json")),
                null, StandardCharsets.UTF_8, new RequestTemplate()
        );
        var ex = new FeignException.BadRequest("bad", feignReq, upstream.getBytes(StandardCharsets.UTF_8), Map.of());

        when(client.getBalance(id)).thenThrow(ex);

        assertThatThrownBy(() -> service.getBalance(id))
                .isInstanceOf(FeignException.BadRequest.class);

        verify(client).getBalance(id);
        verifyNoMoreInteractions(client);
    }

    @Test
    void getTransactions_InvalidDateFormat() {
        long id = 14537780L;
        String from = "2019-01-01";
        String to   = "2019-12-01";

        String upstream = """
        {"status":"KO","errors":[{"code":"REQ017","description":"Invalid date format","params":""}],"payload":{}}
        """;

        var feignReq = Request.create(
                Request.HttpMethod.GET, "/",
                Map.of("Accept", List.of("application/json")),
                null, StandardCharsets.UTF_8, new RequestTemplate()
        );
        var ex = new FeignException.BadRequest("bad", feignReq, upstream.getBytes(StandardCharsets.UTF_8), Map.of());

        when(client.getTransactions(id, from, to)).thenThrow(ex);

        assertThatThrownBy(() -> service.getTransactions(id, from, to))
                .isInstanceOf(FeignException.BadRequest.class);

        verify(client).getTransactions(id, from, to);
        verifyNoMoreInteractions(client);
    }

    @Test
    void createTransfer_FutureDataError() {
        long id = 14537780L;

        var req = new MoneyTransferRequest();
        var creditor = new MoneyTransferRequest.Creditor();
        var account = new MoneyTransferRequest.Account();
        account.setAccountCode("IT23A0336844430152923804660");
        creditor.setName("John");
        creditor.setAccount(account);
        req.setCreditor(creditor);
        req.setDescription("x");
        req.setCurrency("EUR");
        req.setAmount(new BigDecimal("10"));
        req.setExecutionDate(LocalDate.now().plusDays(1));

        String upstream = """
        {"status":"KO","errors":[{"code":"SCT008","description":"La data ordine non puo' essere successiva alla data corrente","params":""}],"payload":{}}
        """;

        var feignReq = Request.create(
                Request.HttpMethod.POST, "/",
                Map.of("Content-Type", List.of("application/json")),
                upstream.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8, new RequestTemplate()
        );
        var ex = new FeignException.BadRequest("bad", feignReq, upstream.getBytes(StandardCharsets.UTF_8), Map.of());

        when(client.createTransfer(eq(id), any(MoneyTransferRequest.class))).thenThrow(ex);

        assertThatThrownBy(() -> service.createTransfer(id, req))
                .isInstanceOf(FeignException.BadRequest.class);

        verify(client).createTransfer(eq(id), any(MoneyTransferRequest.class));
        verifyNoMoreInteractions(client);
    }

}