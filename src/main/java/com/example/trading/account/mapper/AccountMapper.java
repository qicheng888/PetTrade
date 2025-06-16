package com.example.trading.account.mapper;

import com.example.trading.account.model.MerchantAccountTransaction;
import com.example.trading.account.model.UserAccount;
import com.example.trading.account.model.MerchantAccount;
import com.example.trading.account.model.UserAccountTransaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    UserAccount selectUserAccount(Long userId);
    MerchantAccount selectMerchantAccount(Long merchantId);
    void insertUserAccount(UserAccount account);
    void updateUserAccount(UserAccount account);
    void updateMerchantAccount(MerchantAccount account);
    void insertMerchantAccount(MerchantAccount account);
    void deleteMerchantAccount(Long merchantId);

    void insertUserAccountTransaction(UserAccountTransaction transaction);
    /**
     * 查询用户账户的交易记录
     * @param userId 用户ID
     * @return 用户的所有交易记录
     */
    List<UserAccountTransaction> selectTransactionsByUserId(Long userId);


    void insertMerchantAccountTransaction(MerchantAccountTransaction transaction);
    /**
     * 查询商户账户的交易记录
     * @param merchantId 商户ID
     * @return 商户的所有交易记录
     */
    List<MerchantAccountTransaction> selectTransactionsByMerchantId(Long merchantId);


}