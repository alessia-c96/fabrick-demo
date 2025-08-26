package com.example.fabrick_demo.dto;

import java.util.List;

public class TransactionsResponse {

    private String status;
    private Payload payload;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }

    public static class Payload {
        private List<Transaction> list;

        public List<Transaction> getList() { return list; }
        public void setList(List<Transaction> list) { this.list = list; }
    }

    public static class Transaction {
        private String transactionId;
        private String operationId;
        private String accountingDate;
        private String valueDate;
        private Type type;
        private Double amount;
        private String currency;
        private String description;

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

        public String getOperationId() { return operationId; }
        public void setOperationId(String operationId) { this.operationId = operationId; }

        public String getAccountingDate() { return accountingDate; }
        public void setAccountingDate(String accountingDate) { this.accountingDate = accountingDate; }

        public String getValueDate() { return valueDate; }
        public void setValueDate(String valueDate) { this.valueDate = valueDate; }

        public Type getType() { return type; }
        public void setType(Type type) { this.type = type; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Type {
        private String enumeration;
        private String value;

        public String getEnumeration() { return enumeration; }
        public void setEnumeration(String enumeration) { this.enumeration = enumeration; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
