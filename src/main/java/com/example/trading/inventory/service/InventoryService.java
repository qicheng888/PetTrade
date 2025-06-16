package com.example.trading.inventory.service;

import com.example.trading.inventory.mapper.InventoryMapper;
import com.example.trading.inventory.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    public Product createProduct(Product product) {
        inventoryMapper.insertProduct(product);
        return product;
    }

    @Transactional
    public void addInventory(Long productId, int quantity) {
        Product product = inventoryMapper.selectProductById(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        inventoryMapper.updateProductStock(product);
        inventoryMapper.insertInventoryTransaction(productId, quantity, "PURCHASE");
    }

    @Transactional
    public void deductInventory(Long productId, int quantity) {
        Product product = inventoryMapper.selectProductById(productId);
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("库存不足");
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        inventoryMapper.updateProductStock(product);
        inventoryMapper.insertInventoryTransaction(productId, -quantity, "SALE");
    }

    public Product getProduct(Long productId) {
        return inventoryMapper.selectProductById(productId);
    }

    public Product getProductBySku(String sku) {
        return inventoryMapper.selectProductBySku(sku);
    }
}