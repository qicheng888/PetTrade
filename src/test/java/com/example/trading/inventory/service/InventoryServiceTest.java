package com.example.trading.inventory.service;

import com.example.trading.inventory.model.Product;
import com.example.trading.inventory.mapper.InventoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryMapper inventoryMapper;
    
    @InjectMocks
    private InventoryService inventoryService;
    
    @BeforeEach
    void setUp() {
        inventoryMapper = mock(InventoryMapper.class);
        inventoryService = new InventoryService(inventoryMapper);
    }

    @Test
    public void testAddInventory() {
        Long productId = 1L;
        int quantity = 10;
        Product product = new Product();
        product.setProductId(productId);
        product.setStockQuantity(50);
        
        when(inventoryMapper.selectProductById(productId)).thenReturn(product);
        
        inventoryService.addInventory(productId, quantity);
        
        verify(inventoryMapper).updateProductStock(argThat(p -> 
            p.getStockQuantity() == 60
        ));
        verify(inventoryMapper).insertInventoryTransaction(productId, quantity, "PURCHASE");
    }
    
    @Test
    void testCreateProduct() {
        // Given
        Product product = new Product();
        product.setSku("SKU123");
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.99"));
        product.setStockQuantity(100);
        product.setMerchantId(1L);

        // When
        when(inventoryMapper.insertProduct(any(Product.class))).thenReturn(1);
        Product createdProduct = inventoryService.createProduct(product);

        // Then
        assertNotNull(createdProduct);
        assertEquals(product.getSku(), createdProduct.getSku());
        assertEquals(product.getName(), createdProduct.getName());
        assertEquals(product.getPrice(), createdProduct.getPrice());
        assertEquals(product.getStockQuantity(), createdProduct.getStockQuantity());
        assertEquals(product.getMerchantId(), createdProduct.getMerchantId());
        verify(inventoryMapper, times(1)).insertProduct(any(Product.class));
    }
}