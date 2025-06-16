package com.example.trading.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import com.example.trading.account.model.UserAccount;
import com.example.trading.account.model.MerchantAccount;
import com.example.trading.account.model.UserAccountTransaction;
import com.example.trading.account.mapper.AccountMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockitoAnnotations;

public class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        // 初始化操作，如果需要的话
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRecharge_NewAccount() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        when(accountMapper.selectUserAccount(userId)).thenReturn(null);

        // 模拟插入账户时返回生成的 accountId
        doAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            account.setAccountId(888L); // 模拟数据库生成的 ID
            return null;
        }).when(accountMapper).insertUserAccount(any(UserAccount.class));

        UserAccountTransaction expectedTransaction = new UserAccountTransaction();
        expectedTransaction.setAccountId(888L);
        expectedTransaction.setAmount(amount);
        expectedTransaction.setType("RECHARGE");

        accountService.recharge(userId, amount);

        verify(accountMapper).insertUserAccount(any(UserAccount.class));
        verify(accountMapper).insertUserAccountTransaction(eq(expectedTransaction));
    }

    @Test
    public void testRecharge_NewAccount_BalanceVerification() {
        Long userId = 2L;
        BigDecimal amount = new BigDecimal("150.00");

        when(accountMapper.selectUserAccount(userId)).thenReturn(null);

        // 模拟插入账户时返回生成的 accountId
        doAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            account.setAccountId(999L); // 模拟数据库生成的 ID
            return null;
        }).when(accountMapper).insertUserAccount(any(UserAccount.class));

        accountService.recharge(userId, amount);

        // 验证账户创建后余额是否正确
        verify(accountMapper).insertUserAccount(argThat(account ->
            account.getUserId().equals(userId) &&
            account.getBalance().compareTo(amount) == 0 &&
            "CNY".equals(account.getCurrency())
        ));

        UserAccountTransaction expectedTransaction = new UserAccountTransaction();
        expectedTransaction.setAccountId(999L);
        expectedTransaction.setAmount(amount);
        expectedTransaction.setType("RECHARGE");

        // 验证交易记录是否正确（这次应该能匹配）
        verify(accountMapper).insertUserAccountTransaction(eq(expectedTransaction));
    }

    @Test
    public void testRecharge_ExistingAccount() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("50.00");
        UserAccount existingAccount = new UserAccount(userId, new BigDecimal("200.00"), "CNY");
        existingAccount.setAccountId(100L);

        when(accountMapper.selectUserAccount(userId)).thenReturn(existingAccount);

        accountService.recharge(userId, amount);

        verify(accountMapper).updateUserAccount(argThat(account ->
            account.getBalance().compareTo(new BigDecimal("250.00")) == 0
        ));

        UserAccountTransaction expectedTransaction = new UserAccountTransaction();
        expectedTransaction.setAccountId(100L);
        expectedTransaction.setAmount(amount);
        expectedTransaction.setType("RECHARGE");

        verify(accountMapper).insertUserAccountTransaction(eq(expectedTransaction));
    }

    @Test
    public void testRecharge_ExistingAccount_BalanceVerification() {
        Long userId = 3L;
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal rechargeAmount = new BigDecimal("100.00");

        UserAccount existingAccount = new UserAccount(userId, initialBalance, "CNY");
        existingAccount.setAccountId(200L);

        when(accountMapper.selectUserAccount(userId)).thenReturn(existingAccount);

        accountService.recharge(userId, rechargeAmount);

        // 验证账户余额是否正确更新
        verify(accountMapper).updateUserAccount(argThat(account ->
            account.getUserId().equals(userId) &&
            account.getBalance().compareTo(initialBalance.add(rechargeAmount)) == 0 &&
            "CNY".equals(account.getCurrency())
        ));

        UserAccountTransaction expectedTransaction = new UserAccountTransaction();
        expectedTransaction.setAccountId(200L);
        expectedTransaction.setAmount(rechargeAmount);
        expectedTransaction.setType("RECHARGE");

        // 验证交易记录是否正确（这次应该能匹配）
        verify(accountMapper).insertUserAccountTransaction(eq(expectedTransaction));
    }

    @Test
    public void testRecharge_InvalidUserId() {
        Long userId = null;
        BigDecimal amount = new BigDecimal("50.00");

        try {
            accountService.recharge(userId, amount);
        } catch (IllegalArgumentException e) {
            // 验证异常信息是否包含"User ID cannot be null"
            assertTrue(e.getMessage().contains("User ID cannot be null"));
        }

        // 验证accountMapper的方法是否未被调用
        verify(accountMapper, never()).selectUserAccount(anyLong());
        verify(accountMapper, never()).updateUserAccount(any(UserAccount.class));
    }

    @Test
    public void testRecharge_InvalidAmount() {
        Long userId = 1L;
        BigDecimal amount = null;

        try {
            // 模拟调用recharge方法前先验证参数
            accountService.recharge(userId, amount);
        } catch (IllegalArgumentException e) {
            // 验证异常信息是否包含"Amount cannot be null"
            assertTrue(e.getMessage().contains("Amount cannot be null"));
        }

        // 验证accountMapper的方法是否未被调用
        verify(accountMapper, never()).selectUserAccount(anyLong());
        verify(accountMapper, never()).updateUserAccount(any(UserAccount.class));
        verify(accountMapper, never()).insertUserAccount(any(UserAccount.class));

    }

    @Test
    public void testSelectMerchantAccount() {
        // 准备测试数据
        Long merchantId = 2001L;
        BigDecimal initialBalance = new BigDecimal("1000.00");
        String currency = "USD";

        // 创建商家账户
        accountService.createMerchant(merchantId, initialBalance, currency);

        // 模拟查询商家账户
        MerchantAccount expectedAccount = new MerchantAccount(merchantId, initialBalance, currency);
        expectedAccount.setAccountId(3001L); // 模拟数据库生成的 ID
        when(accountMapper.selectMerchantAccount(merchantId)).thenReturn(expectedAccount);

        // 调用被测试的方法
        MerchantAccount account = accountService.selectMerchantAccount(merchantId);

        // 验证结果
        assertNotNull(account);
        assertEquals(expectedAccount.getAccountId(), account.getAccountId());
        assertEquals(merchantId, account.getMerchantId());
        assertEquals(initialBalance, account.getBalance());
        assertEquals(currency, account.getCurrency());
    }
}