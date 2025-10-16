package com.example.fabrick_demo.service;

//import com.example.fabrick_demo.client.FabrickClient;
import com.example.fabrick_demo.client.FabrickReactiveClient;
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
import reactor.core.publisher.Mono;

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
    FabrickReactiveClient reactiveClient;
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

        when(reactiveClient.getBalance(id)).thenReturn(Mono.just(expected));

        var actual = service.getBalance(id).block();

        assertThat(actual).isSameAs(expected);
        verify(reactiveClient).getBalance(id);
        verifyNoMoreInteractions(reactiveClient);
    }

    @Test
    void getTransactions_test() {
        long id = 14537780L;
        String from = "2025-08-01";
        String to = "2025-08-27";
        var expected = new TransactionsResponse();
        expected.setStatus("OK");

        when(reactiveClient.getTransactions(id, from, to)).thenReturn(Mono.just(expected));
        var actual = service.getTransactions(id, from, to).block();


        assertThat(actual).isSameAs(expected);
        verify(reactiveClient).getTransactions(id, from, to);
        verifyNoMoreInteractions(reactiveClient);
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

        when(reactiveClient.createTransfer(id, req)).thenReturn(Mono.just(expected));
        var actual = service.createTransfer(id, req).block();


        assertThat(actual).isSameAs(expected);
        verify(reactiveClient).createTransfer(id, req);
        verifyNoMoreInteractions(reactiveClient);
    }

    @Test
    void getBalance_Exception() {
        long id = 14537780L;

        var ex = new RuntimeException("Upstream error");

        when(reactiveClient.getBalance(id)).thenReturn(Mono.error(ex));

        assertThatThrownBy(() -> service.getBalance(id).block())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Upstream error");

        verify(reactiveClient).getBalance(id);
        verifyNoMoreInteractions(reactiveClient);
    }

    @Test
    void getTransactions_InvalidDateFormat() {
        long id = 14537780L;
        String from = "2019-01-01";
        String to   = "2019-12-01";

        var ex = new RuntimeException("Invalid date format");

        when(reactiveClient.getTransactions(id, from, to)).thenReturn(Mono.error(ex));

        assertThatThrownBy(() -> service.getTransactions(id, from, to).block())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid date format");

        verify(reactiveClient).getTransactions(id, from, to);
        verifyNoMoreInteractions(reactiveClient);
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

        var ex = new RuntimeException("La data ordine non puo' essere successiva alla data corrente");

        when(reactiveClient.createTransfer(eq(id), any(MoneyTransferRequest.class)))
                .thenReturn(Mono.error(ex));

        assertThatThrownBy(() -> service.createTransfer(id, req).block())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La data ordine non puo' essere successiva alla data corrente");

        verify(reactiveClient).createTransfer(eq(id), any(MoneyTransferRequest.class));
        verifyNoMoreInteractions(reactiveClient);
    }

}