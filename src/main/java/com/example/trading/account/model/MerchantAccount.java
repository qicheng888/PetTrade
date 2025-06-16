package com.example.trading.account.model;

import java.math.BigDecimal;

public class MerchantAccount {
    private Long accountId;
    private Long merchantId;
    private BigDecimal balance;
    private String currency;

    // 新增字段 settlementStatus，默认值为 "PENDING"
    private String settlementStatus = "PENDING";

    // Constructor added for creating a new merchant account with specified details
    public MerchantAccount(Long merchantId, BigDecimal balance, String currency) {
        this.merchantId = merchantId;
        this.balance = balance;
        this.currency = currency;
    }

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    // 新增 getter 和 setter 方法
    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }
}
