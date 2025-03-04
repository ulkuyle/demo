package com.example.demo.repository;

import com.example.demo.enums.OrderStatus;
import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    List<Order> findByCustomerIdAndOrderStatus(Long customerId, OrderStatus status);
}
