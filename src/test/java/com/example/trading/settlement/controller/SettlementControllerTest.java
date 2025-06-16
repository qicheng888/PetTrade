package com.example.trading.settlement.controller;

import com.example.trading.settlement.mapper.SettlementMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 结算控制器集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SettlementControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SettlementMapper settlementMapper;

    @Test
    public void testDailySettlementSuccess() {


        settlementMapper.deleteSettlements();

        // 调用每日结算API
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/settlement/daily",
            null,
            Void.class
        );

        // 验证HTTP响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}