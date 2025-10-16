package com.example.fabrick_demo.service;

import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<BalanceResponse> getBalance(Long accountId);
    Mono<TransactionsResponse> getTransactions(Long accountId, String fromAccountingDate, String toAccountingDate);
    Mono<MoneyTransferResponse> createTransfer(Long accountId, MoneyTransferRequest moneyTransferRequest);
}
