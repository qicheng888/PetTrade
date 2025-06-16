package com.example.trading.inventory.model;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class Product {
    private Long productId;
    private String sku;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long merchantId;
}