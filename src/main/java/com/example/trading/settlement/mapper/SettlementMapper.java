package com.example.trading.settlement.mapper;

import com.example.trading.settlement.model.Settlement;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.math.BigDecimal;

@Mapper
public interface SettlementMapper {
    List<Long> selectMerchantIds();
    BigDecimal calculateTotalSales(Long merchantId, String date);
    BigDecimal calculateActualIncome(Long merchantId, String date);
    void insertSettlement(Settlement settlement);
    void updateMerchantStatus(Long merchantId, String status);

    void deleteSettlements();
}