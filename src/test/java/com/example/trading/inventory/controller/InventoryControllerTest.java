package com.example.trading.inventory.controller;

import com.example.trading.inventory.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class InventoryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateProductSuccess() {
        // 准备测试数据
        Product product = new Product();
        product.setSku("SKU123" + System.currentTimeMillis()); // 生成唯一SKU
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);
        product.setMerchantId(1001L);

        // 调用创建商品API
        ResponseEntity<Product> response = restTemplate.postForEntity(
            "/api/inventory/create",
            product,
            Product.class
        );

        // 验证HTTP响应状态
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 验证返回的商品信息是否正确
        Product createdProduct = response.getBody();
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getSku()).isEqualTo(product.getSku());
        assertThat(createdProduct.getName()).isEqualTo("Test Product");
        assertThat(createdProduct.getPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(createdProduct.getStockQuantity()).isEqualTo(10);
        assertThat(createdProduct.getMerchantId()).isEqualTo(1001L);
    }

    @Test
    public void testAddInventorySuccess() {
        // 准备测试数据 - 商品ID和增加数量
        Long productId = 1L;
        int quantityToAdd = 5;

        // 调用添加库存API
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/inventory/add?productId={productId}&quantity={quantity}",
            null,
            Void.class,
            productId,
            quantityToAdd
        );

        // 验证HTTP响应状态
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // 其他可能的测试方法可以在此添加
}