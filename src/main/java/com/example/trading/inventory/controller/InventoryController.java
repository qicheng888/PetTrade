package com.example.trading.inventory.controller;

import com.example.trading.inventory.service.InventoryService;
import com.example.trading.inventory.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = inventoryService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    /**
     * 添加库存API
     * @param productId 商品ID
     * @param quantity 增加数量
     */
    @PostMapping("/add")
    public void addInventory(@RequestParam Long productId, 
                             @RequestParam int quantity) {
        inventoryService.addInventory(productId, quantity);
    }
}