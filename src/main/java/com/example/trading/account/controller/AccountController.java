package com.example.trading.account.controller;

import com.example.trading.account.service.AccountService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 用户账户充值API
     * @param userId 用户ID
     * @param amount 充值金额
     */
    @PostMapping("/recharge")
    public void recharge(@RequestParam Long userId, 
                         @RequestParam BigDecimal amount) {
        accountService.recharge(userId, amount);
    }

    /**
     * 创建新的商家账户API
     * @param merchantId 商家ID
     * @param initialBalance 初始余额
     * @param currency 货币类型
     */
    @PostMapping("/create-merchant")
    public void createMerchant(@RequestParam Long merchantId,
                               @RequestParam BigDecimal initialBalance,
                               @RequestParam String currency) {
        accountService.createMerchant(merchantId, initialBalance, currency);
    }
}

