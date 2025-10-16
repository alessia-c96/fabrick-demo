/*package com.example.fabrick_demo.client;

import com.example.fabrick_demo.config.RestClientConfig;
import com.example.fabrick_demo.dto.BalanceResponse;
import com.example.fabrick_demo.dto.MoneyTransferRequest;
import com.example.fabrick_demo.dto.MoneyTransferResponse;
import com.example.fabrick_demo.dto.TransactionsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "fabrickClient",
        url = "${clients.fabrick.base-url}",
        configuration = RestClientConfig.class
)
public interface FabrickClient {

    @GetMapping("/v4.0/accounts/{accountId}/balance")
    BalanceResponse  getBalance(@PathVariable Long accountId);

    @GetMapping("/v4.0/accounts/{accountId}/transactions")
    TransactionsResponse getTransactions(@PathVariable Long accountId,
                                         @RequestParam("fromAccountingDate") String fromAccountingDate,
                                         @RequestParam("toAccountingDate") String toAccountingDate);

    @PostMapping("/v4.0/accounts/{accountId}/payments/money-transfers")
    MoneyTransferResponse createTransfer(@PathVariable Long accountId,
                                         @RequestBody MoneyTransferRequest moneyTransferRequest);
}
*/