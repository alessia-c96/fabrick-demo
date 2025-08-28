package com.example.fabrick_demo.dto;

import java.math.BigDecimal;

public class BalanceResponse {

    private String status;
    private Payload payload;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }

    public static class Payload {
        private String date;
        private BigDecimal balance;
        private BigDecimal availableBalance;
        private String currency;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(BigDecimal availableBalance) {
            this.availableBalance = availableBalance;
        }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}
