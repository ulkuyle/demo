package com.example.demo.service.strategy;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderSide;

public interface OrderSideHandling {
    void handleOrder(OrderDTO order);
    OrderSide getOrderSide();
}
