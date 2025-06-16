package com.example.trading.settlement.controller;

import com.example.trading.settlement.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算控制器
 */
@RestController
public class SettlementController {
    @Autowired
    private SettlementService settlementService;

    @PostMapping("/api/settlement/daily")
    public void triggerDailySettlement() {
        settlementService.dailySettlement();
    }
}