package com.example.fabrick_demo.client;

import com.example.fabrick_demo.dto.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class FabrickReactiveClient {

    private final WebClient webClient;

    public FabrickReactiveClient(@Qualifier("fabrickWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BalanceResponse> getBalance(Long accountId) {
        return webClient.get()
                .uri("/v4.0/accounts/{accountId}/balance", accountId)
                .retrieve()
                .bodyToMono(BalanceResponse.class);
    }

    public Mono<TransactionsResponse> getTransactions(Long accountId, String fromAccountingDate, String toAccountingDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v4.0/accounts/{accountId}/transactions")
                        .queryParam("fromAccountingDate", fromAccountingDate)
                        .queryParam("toAccountingDate", toAccountingDate)
                        .build(accountId))
                .retrieve()
                .bodyToMono(TransactionsResponse.class);
    }

    public Mono<MoneyTransferResponse> createTransfer(Long accountId, MoneyTransferRequest request) {
        return webClient.post()
                .uri("/v4.0/accounts/{accountId}/payments/money-transfers", accountId)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    HttpStatusCode status = response.statusCode();
                                    return Mono.error(new ResponseStatusException(status, body));
                                })
                )
                .bodyToMono(MoneyTransferResponse.class);
    }
}