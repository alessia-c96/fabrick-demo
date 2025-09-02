package com.example.fabrick_demo.client;

import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "clients.fabrick.base-url=http://localhost:${wiremock.server.port}",
        "clients.fabrick.api-key=dummy",
        "clients.fabrick.auth-schema=S2S"
})
public class FabrickClientWireMock {

    @Autowired FabrickClient client;

    @Test
    void balance_ok() {
        stubFor(get(urlPathEqualTo("/v4.0/accounts/14537780/balance"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("""
          {
            "status":"OK",
            "payload":{"availableBalance":100.00,"currency":"EUR"}
          }
          """)));

        var r = client.getBalance(14537780L);
        assertThat(r.getStatus()).isEqualTo("OK");
        assertThat(r.getPayload().getAvailableBalance())
                .isEqualByComparingTo("100.00");
    }

    @Test
    void transactions_ok() {
        stubFor(get(urlPathEqualTo("/v4.0/accounts/14537780/transactions"))
                .withQueryParam("fromAccountingDate", equalTo("2025-08-01"))
                .withQueryParam("toAccountingDate",   equalTo("2025-08-27"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("""
          {
            "status":"OK",
            "payload":{"list":[]}
          }
          """)));

        var r = client.getTransactions(14537780L, "2025-08-01", "2025-08-27");
        assertThat(r.getStatus()).isEqualTo("OK");
        assertThat(r.getPayload().getList()).isEmpty();
    }

    @Test
    void createTransfer_ok() {
        stubFor(post(urlPathEqualTo("/v4.0/accounts/14537780/payments/money-transfers"))
                .withRequestBody(matchingJsonPath("$.creditor.name", equalTo("John Doe")))
                .withRequestBody(matchingJsonPath("$.amount", equalTo("800")))
                .withRequestBody(matchingJsonPath("$.executionDate", matching("\\d{4}-\\d{2}-\\d{2}")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("""
          {
            "code":"OK",
            "description":"Transfer accepted"
          }
          """)));

        var req = new MoneyTransferRequest();
        var acc = new MoneyTransferRequest.Account(); acc.setAccountCode("IT23A0336844430152923804660");
        var cred = new MoneyTransferRequest.Creditor(); cred.setName("John Doe"); cred.setAccount(acc);
        req.setCreditor(cred);
        req.setDescription("Payment invoice 75/2017");
        req.setCurrency("EUR");
        req.setAmount(new BigDecimal("800"));
        req.setExecutionDate(LocalDate.of(2025,8,27));

        MoneyTransferResponse r = client.createTransfer(14537780L, req);
        assertThat(r.getCode()).isEqualTo("OK");
    }

    @Test
    void balance_400() {
        stubFor(get(urlPathEqualTo("/v4.0/accounts/14537780/balance"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type","application/json")
                        .withBody("""
          {
            "status":"KO",
            "errors":[{"code":"GEN001","description":"Errore generico"}]
          }
          """)));

        assertThatThrownBy(() -> client.getBalance(14537780L))
                .isInstanceOf(feign.FeignException.BadRequest.class);
    }
}
