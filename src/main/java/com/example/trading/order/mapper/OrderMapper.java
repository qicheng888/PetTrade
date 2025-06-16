package com.example.trading.order.mapper;

import com.example.trading.order.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    void insertOrder(Order order);
}