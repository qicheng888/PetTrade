package com.example.trading.settlement.service;

import com.example.trading.settlement.model.Settlement;
import com.example.trading.settlement.mapper.SettlementMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SettlementService {

    private final SettlementMapper settlementMapper;

    public SettlementService(SettlementMapper settlementMapper) {
        this.settlementMapper = settlementMapper;
    }

    /**
     * 每日结算对账任务
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void dailySettlement() {
        LocalDate settlementDate = LocalDate.now().minusDays(1); // 结算前一天
        // 获取所有需要结算的商家
        List<Long> merchantIds = settlementMapper.selectMerchantIds();
        
        for (Long merchantId : merchantIds) {
            // 计算商品销售总额
            BigDecimal totalSales = settlementMapper.calculateTotalSales(merchantId, settlementDate.toString());
            // 计算实际账户收入
            BigDecimal actualIncome = settlementMapper.calculateActualIncome(merchantId, settlementDate.toString());
            
            // 创建结算记录
            Settlement settlement = new Settlement(
                merchantId, 
                settlementDate, 
                totalSales, 
                actualIncome, 
                totalSales.compareTo(actualIncome) == 0 ? "MATCHED" : "MISMATCH"
            );
            settlementMapper.insertSettlement(settlement);
            
            // 如果匹配成功，更新商家结算状态
            if ("MATCHED".equals(settlement.getStatus())) {
                settlementMapper.updateMerchantStatus(merchantId, "COMPLETED");
            }
        }
    }
}