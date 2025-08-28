package com.example.fabrick_demo.service;

import com.example.fabrick_demo.client.FabrickClient;
import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    FabrickClient client;

    @Override public BalanceResponse getBalance(Long accountId) {
        return client.getBalance(accountId);
    }
    @Override public TransactionsResponse getTransactions(Long accountId, String from, String to) {
        return client.getTransactions(accountId, from, to);
    }
    @Override public MoneyTransferResponse createTransfer(Long accountId, MoneyTransferRequest moneyTransferRequest) {
        return client.createTransfer(accountId, moneyTransferRequest);
    }
}
