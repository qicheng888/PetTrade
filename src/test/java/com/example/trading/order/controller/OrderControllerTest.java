package com.example.trading.order.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.example.trading.order.service.OrderService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

// 新建测试类
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Test
    public void testCreateOrderSuccess() {
        // 准备测试数据
        Long userId = 1001L;
        Long merchantId = 2002L;
        Long productId = 1L;
        int quantity = 2;

        // 调用创建订单API
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/orders?userId={userId}&merchantId={merchantId}&productId={productId}&quantity={quantity}",
            null,
            Void.class,
            userId,
            merchantId,
            productId,
            quantity
        );

        // 验证HTTP响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
    }

}