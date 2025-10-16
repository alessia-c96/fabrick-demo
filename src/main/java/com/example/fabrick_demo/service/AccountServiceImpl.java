package com.example.fabrick_demo.service;

//import com.example.fabrick_demo.client.FabrickClient;
import com.example.fabrick_demo.client.FabrickReactiveClient;
import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {

    private final FabrickReactiveClient fabrickClient;

    public AccountServiceImpl(FabrickReactiveClient fabrickClient) {
        this.fabrickClient = fabrickClient;
    }

    @Override
    public Mono<BalanceResponse> getBalance(Long accountId) {
        return fabrickClient.getBalance(accountId);
    }

    @Override
    public Mono<TransactionsResponse> getTransactions(Long accountId, String from, String to) {
        return fabrickClient.getTransactions(accountId, from, to);
    }

    @Override
    public Mono<MoneyTransferResponse> createTransfer(Long accountId, MoneyTransferRequest moneyTransferRequest) {
        return fabrickClient.createTransfer(accountId, moneyTransferRequest);
    }
}
