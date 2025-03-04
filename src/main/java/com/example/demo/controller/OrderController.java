package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Operations related to orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value ="/create")
    private ResponseEntity<String> createOrder(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody OrderDTO order) {
        return ResponseEntity.ok(orderService.createOrder(authorizationHeader, order));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value ="/match")
    public void matchOrders(@RequestParam Long customerId)  {
        orderService.matchPendingOrders(customerId);
    }

    @GetMapping(value ="/list")
    public ResponseEntity<?> listOrders(@RequestHeader("Authorization") String authorizationHeader, @RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(authorizationHeader, customerId));
    }

    @DeleteMapping(value ="/delete")
    public ResponseEntity<String> deleteOrder(@RequestHeader("Authorization") String authorizationHeader, @RequestParam Long orderId, @RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.deleteOrder(authorizationHeader, orderId, customerId));
    }
}
