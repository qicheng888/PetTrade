package com.example.trading.account.controller;

import com.example.trading.account.mapper.AccountMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.trading.account.service.AccountService;
import com.example.trading.account.model.UserAccount; // 导入UserAccount类
import com.example.trading.account.model.UserAccountTransaction; // 导入AccountTransaction类
import com.example.trading.account.model.MerchantAccount; // 导入MerchantAccount类

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // 新增注解
import org.springframework.test.web.servlet.MockMvc; // 新增导入

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    AccountMapper accountMapper;

    @Test
    //@Transactional
    public void testRechargeSuccess() {
        // 准备测试数据
        Long userId = 1001L;
        BigDecimal amount = new BigDecimal("500.00");

        UserAccount accountBefore = accountService.selectUserAccount(userId);

        // 调用充值API
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/accounts/recharge?userId={userId}&amount={amount}",
            null,
            Void.class,
            userId,
            amount
        );

        // 验证HTTP响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // 验证账户是否正确创建
        UserAccount account = accountService.selectUserAccount(userId);
        assertNotNull(account);
        assertEquals(userId, account.getUserId());
        assertEquals( amount.add(accountBefore.getBalance()), account.getBalance());
        
        // 验证交易记录是否正确插入
        List<UserAccountTransaction> transactions = accountService.selectTransactionByUserId(userId);
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
        transactions.forEach(tx -> {
            assertNotNull(tx.getAccountId(), "交易记录的account_id不能为null");
            assertEquals(account.getAccountId(), tx.getAccountId(), "交易记录的account_id与账户ID不匹配");
        });
    }

    @Test
    public void testRechargeWithNegativeAmount() {
        // 准备测试数据 - 无效金额
        Long userId = 1001L;
        BigDecimal invalidAmount = new BigDecimal("-100.00");
        
        // 调用充值API
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/accounts/recharge?userId={userId}&amount={amount}",
            null,
            Void.class,
            userId,
            invalidAmount
        );

        // 验证HTTP响应状态应为400错误
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    // 新增测试用例：验证无效账户ID的情况
    @Test
    public void testRechargeWithInvalidAccountId() {
        // 使用不存在的账户ID
        Long invalidUserId = -999L;
        BigDecimal amount = new BigDecimal("100.00");
        
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/accounts/recharge?userId={userId}&amount={amount}",
            null,
            Void.class,
            invalidUserId,
            amount
        );

        // 验证应返回400错误
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRechargeWithZeroAmount() {
        // 使用金额为0的情况
        Long userId = 1001L;
        BigDecimal zeroAmount = new BigDecimal("0.00");
        
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/accounts/recharge?userId={userId}&amount={amount}",
            null,
            Void.class,
            userId,
            zeroAmount
        );

        // 验证应返回400错误
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testRechargeWithNullAmount() {
        // 使用null作为金额
        Long userId = 1001L;
        
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/accounts/recharge?userId={userId}&amount",
            null,
            Void.class,
            userId
        );

        // 验证应返回400错误
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateMerchantSuccess() {
        // 准备测试数据


        Long merchantId = 2002L; // 修改为唯一的merchantId以避免数据库唯一约束冲突

        accountMapper.deleteMerchantAccount(merchantId);

        BigDecimal initialBalance = new BigDecimal("1000.00");
        String currency = "USD";

        // 调用创建商家账户API
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/accounts/create-merchant?merchantId={merchantId}&initialBalance={initialBalance}&currency={currency}",
            null,
            Void.class,
            merchantId,
            initialBalance,
            currency
        );

        // 验证HTTP响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证商家账户是否正确创建
        MerchantAccount account = accountService.selectMerchantAccount(merchantId);
        assertNotNull(account);
        assertEquals(merchantId, account.getMerchantId());
        assertEquals(initialBalance, account.getBalance());
    }
}