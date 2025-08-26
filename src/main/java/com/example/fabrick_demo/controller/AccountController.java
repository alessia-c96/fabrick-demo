package com.example.fabrick_demo.controller;

import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import com.example.fabrick_demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gbs/banking/v4.0")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/accounts/{accountId}/balance")
    public BalanceResponse getBalance(@PathVariable Long accountId) {
        return accountService.getBalance(accountId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public TransactionsResponse getTransactions(
            @PathVariable Long accountId,
            @RequestParam("fromAccountingDate") String from,
            @RequestParam("toAccountingDate") String to
    ) {
        return accountService.getTransactions(accountId, from, to);
    }

    @PostMapping("/accounts/{accountId}/payments/money-transfers")
    public MoneyTransferResponse createTransfer(
            @PathVariable Long accountId,
            @RequestBody MoneyTransferRequest moneyTransferRequest
    ) {
        return accountService.createTransfer(accountId, moneyTransferRequest);
    }
}
