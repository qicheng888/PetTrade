package com.example.trading.order.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private Long userId;
    private Long merchantId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;
    private BigDecimal totalAmount;

    // 构造函数
    public Order(Long userId, Long merchantId, Long productId, 
                String productName, BigDecimal productPrice,
                int quantity, BigDecimal totalAmount) {
        this.userId = userId;
        this.merchantId = merchantId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

}