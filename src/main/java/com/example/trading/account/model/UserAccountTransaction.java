package com.example.trading.account.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户交易记录模型
 */
@Data
public class UserAccountTransaction {
    private Long transactionId;
    private Long accountId;
    private BigDecimal amount;
    private String type;
    private Long orderId;
    private Date createdAt;
}