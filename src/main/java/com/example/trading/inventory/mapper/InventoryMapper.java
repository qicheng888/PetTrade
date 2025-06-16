package com.example.trading.inventory.mapper;

import com.example.trading.inventory.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InventoryMapper {
    @Select("SELECT * FROM products WHERE product_id = #{productId}")
    Product selectProductById(Long productId);

    @Select("SELECT * FROM products WHERE sku = #{sku}")
    Product selectProductBySku(String sku);

    int insertProduct(Product product);
    void updateProductStock(Product product);
    void insertInventoryTransaction(Long productId, int quantity, String type);
}