package com.example.trading.account.service;

import com.example.trading.account.model.MerchantAccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

import com.example.trading.account.model.UserAccount;
import com.example.trading.account.model.MerchantAccount;
import com.example.trading.account.model.UserAccountTransaction;
import com.example.trading.account.mapper.AccountMapper;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 用户账户充值
     * @param userId 用户ID
     * @param amount 充值金额
     */
    @Transactional
    public void recharge(Long userId, BigDecimal amount) {
        // 验证关键参数是否为空
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (userId < 0) {
            throw new IllegalArgumentException("User ID cannot be negative");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0 ) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }

        UserAccount account = accountMapper.selectUserAccount(userId);
        if (account == null) {
            account = new UserAccount(userId, amount, "CNY");
            accountMapper.insertUserAccount(account);
            // 直接使用插入后的 account 对象，MyBatis 会自动填充 accountId
        } else {
            account.setBalance(account.getBalance().add(amount));
            accountMapper.updateUserAccount(account);
        }

        UserAccountTransaction transaction = new UserAccountTransaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setAmount(amount);
        transaction.setType("RECHARGE");
        accountMapper.insertUserAccountTransaction(transaction);
    }

    /**
     * 处理支付交易
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @param amount 交易金额
     */
    @Transactional
    public void processPayment(Long userId, Long merchantId, BigDecimal amount) {
        UserAccount userAccount = accountMapper.selectUserAccount(userId);
        MerchantAccount merchantAccount = accountMapper.selectMerchantAccount(merchantId);
        
        // 扣减用户余额
        userAccount.setBalance(userAccount.getBalance().subtract(amount));
        accountMapper.updateUserAccount(userAccount);
        
        UserAccountTransaction userTransaction = new UserAccountTransaction();
        userTransaction.setAccountId(userAccount.getAccountId());
        userTransaction.setAmount(amount.negate());
        userTransaction.setType("PAYMENT");
        accountMapper.insertUserAccountTransaction(userTransaction);
        
        // 增加商家余额
        merchantAccount.setBalance(merchantAccount.getBalance().add(amount));
        accountMapper.updateMerchantAccount(merchantAccount);
        
        MerchantAccountTransaction merchantTransaction = new MerchantAccountTransaction();
        merchantTransaction.setAccountId(merchantAccount.getAccountId());
        merchantTransaction.setAmount(amount);
        merchantTransaction.setType("PAYMENT");
        accountMapper.insertMerchantAccountTransaction(merchantTransaction);
    }
    
    /**
     * 查询用户账户的交易记录
     * @param userId 用户ID
     * @return 用户的所有交易记录
     */
    public List<UserAccountTransaction> selectTransactionByUserId(Long userId) {
        return accountMapper.selectTransactionsByUserId(userId);
    }
    
    /**
     * 查询用户账户信息
     * @param userId 用户ID
     * @return 用户账户信息
     */
    public UserAccount selectUserAccount(Long userId) {
        return accountMapper.selectUserAccount(userId);
    }

    /**
     * 查询商家账户信息
     * @param merchantId 商家ID
     * @return 商家账户信息
     */
    public MerchantAccount selectMerchantAccount(Long merchantId) {
        return accountMapper.selectMerchantAccount(merchantId);
    }

    /**
     * 创建新的商家账户
     * @param merchantId 商家ID
     * @param initialBalance 初始余额
     * @param currency 货币类型
     */
    @Transactional
    public void createMerchant(Long merchantId, BigDecimal initialBalance, String currency) {
        // 验证关键参数是否为空
        if (merchantId == null) {
            throw new IllegalArgumentException("Merchant ID cannot be null");
        }

        if (merchantId < 0) {
            throw new IllegalArgumentException("Merchant ID cannot be negative");
        }

        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }

        MerchantAccount account = new MerchantAccount(merchantId, initialBalance, currency);
        accountMapper.insertMerchantAccount(account);
    }
}