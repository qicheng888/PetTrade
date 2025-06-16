package com.example.trading.order.service;

import com.example.trading.account.service.AccountService;
import com.example.trading.inventory.service.InventoryService;
import com.example.trading.inventory.model.Product;
import com.example.trading.inventory.mapper.InventoryMapper;
import com.example.trading.order.model.Order;
import com.example.trading.order.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private OrderMapper orderMapper;
    @Mock private AccountService accountService;
    @Mock private InventoryService inventoryService;
    @Mock private InventoryMapper inventoryMapper;
    
    @InjectMocks
    private OrderService orderService;

    @Test
    public void testCreateOrder_Success() {
        Long userId = 1L;
        Long merchantId = 2L;
        Long productId = 3L;
        int quantity = 5;
        
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);
        
        when(inventoryMapper.selectProductById(productId)).thenReturn(product);
        
        orderService.createOrder(userId, merchantId, productId, quantity);
        
        // 验证订单是否正确创建
        verify(orderMapper).insertOrder(argThat(order -> order.getUserId().equals(userId)));
        verify(orderMapper).insertOrder(argThat(order -> order.getMerchantId().equals(merchantId)));
        verify(orderMapper).insertOrder(argThat(order -> order.getProductId().equals(productId)));
        verify(orderMapper).insertOrder(argThat(order -> order.getProductName().equals("Test Product")));
        verify(orderMapper).insertOrder(argThat(order -> order.getProductPrice().compareTo(new BigDecimal("100.00")) == 0));
        verify(orderMapper).insertOrder(argThat(order -> order.getQuantity() == quantity));
        verify(orderMapper).insertOrder(argThat(order -> order.getTotalAmount().compareTo(new BigDecimal("500.00")) == 0));
        
        // 验证支付是否正确处理
        verify(accountService).processPayment(userId, merchantId, new BigDecimal("500.00"));
        
        // 验证库存是否正确扣减
        verify(inventoryService).deductInventory(productId, quantity);
    }

    @Test
    public void testCreateOrder_InsufficientStock() {
        Long userId = 1L;
        Long merchantId = 2L;
        Long productId = 3L;
        int quantity = 20;
        
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);
        
        // 模拟查询产品时返回库存不足的产品
        when(inventoryMapper.selectProductById(productId)).thenReturn(product);
        
        // 验证是否抛出异常
        assertThrows(RuntimeException.class, () -> 
            orderService.createOrder(userId, merchantId, productId, quantity)
        );
        
        // 验证订单是否未被创建
        verify(orderMapper, never()).insertOrder(any(Order.class));
        
        // 验证支付是否未被处理
        verify(accountService, never()).processPayment(eq(userId), eq(merchantId), eq(new BigDecimal("500.00")));
        
    }
}