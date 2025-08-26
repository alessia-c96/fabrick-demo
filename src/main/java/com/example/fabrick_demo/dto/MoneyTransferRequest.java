package com.example.fabrick_demo.dto;

import java.math.BigDecimal;

public class MoneyTransferRequest {
    private Creditor creditor;
    private String description;
    private String currency;
    private BigDecimal amount;
    private String executionDate; //YYYY-MM-DD

    public Creditor getCreditor() {
        return creditor;
    }

    public void setCreditor(Creditor creditor) {
        this.creditor = creditor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(String executionDate) {
        this.executionDate = executionDate;
    }

    public static class Creditor {
        private String name;
        private Account account;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }
    }

    public static class Account {
        private String accountCode;

        public String getAccountCode() {
            return accountCode;
        }

        public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
        }
    }
}
