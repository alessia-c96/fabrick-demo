package com.example.fabrick_demo.controller;

import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import com.example.fabrick_demo.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/api/gbs/banking/v4.0")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts/{accountId}/balance")
    public Mono<BalanceResponse> getBalance(@PathVariable Long accountId) {
        return accountService.getBalance(accountId);
    }


    @GetMapping("/accounts/{accountId}/transactions")
    public Mono<TransactionsResponse> getTransactions(
            @PathVariable Long accountId,
            @RequestParam("fromAccountingDate")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Deve essere YYYY-MM-DD")
            String fromAccountingDate,
            @RequestParam("toAccountingDate")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Deve essere YYYY-MM-DD")
            String toAccountingDate
    ) {
        return accountService.getTransactions(accountId, fromAccountingDate, toAccountingDate);
    }

    @PostMapping("/accounts/{accountId}/payments/money-transfers")
    public Mono<MoneyTransferResponse> createTransfer(
            @PathVariable Long accountId,
            @Valid @RequestBody MoneyTransferRequest moneyTransferRequest
    ) {
        return accountService.createTransfer(accountId, moneyTransferRequest);
    }
}
