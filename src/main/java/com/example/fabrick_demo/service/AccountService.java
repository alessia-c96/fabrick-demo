package com.example.fabrick_demo.service;

import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;

public interface AccountService {
    BalanceResponse getBalance(Long accountId);
    TransactionsResponse getTransactions(Long accountId, String from, String to);
    MoneyTransferResponse createTransfer(Long accountId, MoneyTransferRequest moneyTransferRequest);
}
