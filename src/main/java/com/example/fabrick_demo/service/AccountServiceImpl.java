package com.example.fabrick_demo.service;

import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountServiceImpl implements AccountService {

    private final RestTemplate restTemplate;

    public AccountServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${fabrick.base-url}")
    private String baseUrl;

    @Value("${fabrick.api-key}")
    private String apiKey;

    @Value("${fabrick.auth-schema}")
    private String authSchema;

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Auth-Schema", authSchema);
        headers.set("Api-Key", apiKey);
        headers.set("X-Time-Zone", "Europe/Rome");
        headers.set("X-Request-Id", java.util.UUID.randomUUID().toString());
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    @Override
    public BalanceResponse getBalance(Long accountId) {
        String url = baseUrl + "/api/gbs/banking/v4.0/accounts/" + accountId + "/balance";
        ResponseEntity<BalanceResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(defaultHeaders()), BalanceResponse.class);
        return resp.getBody();
    }

    @Override
    public TransactionsResponse getTransactions(Long accountId, String from, String to) {
        String url = baseUrl + "/api/gbs/banking/v4.0/accounts/" + accountId
                + "/transactions?fromAccountingDate=" + from + "&toAccountingDate=" + to;
        ResponseEntity<TransactionsResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(defaultHeaders()), TransactionsResponse.class);
        return resp.getBody();
    }

    @Override
    public MoneyTransferResponse createTransfer(Long accountId, MoneyTransferRequest body) {
        String url = baseUrl + "/api/gbs/banking/v4.0/accounts/" + accountId + "/payments/money-transfers";
        HttpHeaders headers = defaultHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<MoneyTransferResponse> resp = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(body, headers), MoneyTransferResponse.class);
            return resp.getBody();
        } catch (RestClientResponseException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.valueOf(ex.getRawStatusCode()), ex.getResponseBodyAsString(), ex);
        }
    }
}
