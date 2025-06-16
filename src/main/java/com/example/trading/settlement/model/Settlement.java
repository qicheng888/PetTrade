package com.example.trading.settlement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Settlement {
    private Long merchantId;
    private LocalDate settlementDate;
    private BigDecimal totalSales;
    private BigDecimal actualIncome;
    private String status;

    // 构造函数
    public Settlement(Long merchantId, LocalDate settlementDate, 
                     BigDecimal totalSales, BigDecimal actualIncome, 
                     String status) {
        this.merchantId = merchantId;
        this.settlementDate = settlementDate;
        this.totalSales = totalSales;
        this.actualIncome = actualIncome;
        this.status = status;
    }

    // Getters and Setters
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public LocalDate getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDate settlementDate) { this.settlementDate = settlementDate; }
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public BigDecimal getActualIncome() { return actualIncome; }
    public void setActualIncome(BigDecimal actualIncome) { this.actualIncome = actualIncome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}