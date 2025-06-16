package com.example.trading.order.controller;

import com.example.trading.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单API
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @param productId 商品ID
     * @param quantity 购买数量
     */
    @PostMapping
    public void createOrder(@RequestParam Long userId,
                            @RequestParam Long merchantId,
                            @RequestParam Long productId,
                            @RequestParam int quantity) {
        orderService.createOrder(userId, merchantId, productId, quantity);
    }
}