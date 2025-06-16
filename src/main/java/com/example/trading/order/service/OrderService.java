package com.example.trading.order.service;

import com.example.trading.account.service.AccountService;
import com.example.trading.inventory.service.InventoryService;
import com.example.trading.inventory.model.Product; // 确保导入Product类
import com.example.trading.order.model.Order; // 确保导入Order类
import com.example.trading.order.mapper.OrderMapper; // 新增导入
import com.example.trading.inventory.mapper.InventoryMapper; // 新增导入
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final AccountService accountService;
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    public OrderService(OrderMapper orderMapper,
                        AccountService accountService, 
                        InventoryService inventoryService,
                        InventoryMapper inventoryMapper) {
        this.orderMapper = orderMapper;
        this.accountService = accountService;
        this.inventoryService = inventoryService;
        this.inventoryMapper = inventoryMapper;
    }

    /**
     * 创建订单并完成支付
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @param productId 商品ID
     * @param quantity 购买数量
     */
    @Transactional
    public void createOrder(Long userId, Long merchantId, Long productId, int quantity) {
        Product product = inventoryMapper.selectProductById(productId); // 使用Mapper查询
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        
        // 创建订单
        Order order = new Order(userId, merchantId, productId, 
                               product.getName(), product.getPrice(), 
                               quantity, totalAmount);
        orderMapper.insertOrder(order); // 使用Mapper插入
        
        // 处理支付
        accountService.processPayment(userId, merchantId, totalAmount);
        
        // 扣减库存
        inventoryService.deductInventory(productId, quantity);
    }
}