package com.example.demo.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderSide;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.model.Asset;
import com.example.demo.model.Order;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.strategy.BuyOrder;
import com.example.demo.service.strategy.OrderSideHandling;
import com.example.demo.service.strategy.SellOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BuyOrder buyOrder;

    @Mock
    private SellOrder sellOrder;

    @Mock
    private AuthService authService;

    private OrderService orderService;

    private OrderDTO validOrderDTO;

    private Asset tryAsset;

    private String authHeader;

    @BeforeEach
    public void setUp() throws AccessDeniedException {
        MockitoAnnotations.openMocks(this);
        when(buyOrder.getOrderSide()).thenReturn(OrderSide.BUY);
        when(sellOrder.getOrderSide()).thenReturn(OrderSide.SELL);

        List<OrderSideHandling> strategies = List.of(buyOrder, sellOrder);
        orderService = new OrderService(orderRepository, assetRepository, authService, strategies);

        authHeader = "Bearer token";
        lenient().doNothing().when(authService).checkIfUserAuthorized(authHeader, 1L);

        validOrderDTO = OrderDTO.builder()
                .customerId(1L)
                .assetName("BTC")
                .size(1)
                .orderSide(OrderSide.BUY)
                .price(BigDecimal.valueOf(100))
                .build();

        tryAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .usableSize(100)
                .build();
    }

    @Test
    public void givenValidOrder_whenCreateOrder_thenSuccess() {
        String result = orderService.createOrder(authHeader, validOrderDTO);
        assertEquals("Order created successfully!", result);
    }

    @Test
    void givenValidBuyOrder_whenDeleteOrder_thenReturnSuccessMessage() {

        Order buyOrder = Order.builder().id(1L).customerId(1L).orderStatus(OrderStatus.PENDING).orderSide(OrderSide.BUY)
                .assetName("BTC").size(2).price(BigDecimal.valueOf(50000))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(buyOrder));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        String result = orderService.deleteOrder(authHeader, 1L, 1L);

        verify(assetRepository).save(any(Asset.class));
        verify(orderRepository).delete(buyOrder);

        assertEquals("Order deleted successfully!", result);
    }

    @Test
    void givenNonExistingOrder_whenDeleteOrder_thenThrowIllegalArgumentException() {
        String authHeader = "Bearer token";
        when(orderRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderService.deleteOrder(authHeader, 3L, 1L)
        );
    }

    @Test
    void givenNonPendingOrder_whenDeleteOrder_thenThrowIllegalStateException() {
        String authHeader = "Bearer token";

        Order completedOrder = Order.builder().id(4L).customerId(1L).orderStatus(OrderStatus.MATCHED)
                .orderSide(OrderSide.BUY).build();

        when(orderRepository.findById(4L)).thenReturn(Optional.of(completedOrder));
        assertThrows(IllegalStateException.class, () ->
                orderService.deleteOrder(authHeader, 4L, 1L)
        );
    }

    @Test
    void givenValidAuthorization_whenGetOrdersByCustomer_thenReturnOrderList() {

        List<Order> mockOrders = List.of(Order.builder().id(1L).customerId(1L)
                        .orderSide(OrderSide.BUY).orderStatus(OrderStatus.PENDING).assetName("BTC")
                        .size(2).price(BigDecimal.valueOf(50000)).build(),
                Order.builder().id(2L).customerId(1L).orderSide(OrderSide.SELL)
                        .orderStatus(OrderStatus.CANCELLED).assetName("ETH").size(1)
                        .price(BigDecimal.valueOf(2000)).build()
        );

        when(orderRepository.findByCustomerId(1L)).thenReturn(mockOrders);

        List<Order> result = orderService.getOrdersByCustomer(authHeader, 1L);

        verify(orderRepository).findByCustomerId(1L);

        assertEquals(2, result.size());
        assertEquals("BTC", result.get(0).getAssetName());
        assertEquals("ETH", result.get(1).getAssetName());
    }

}
