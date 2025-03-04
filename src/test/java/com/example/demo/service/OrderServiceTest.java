package com.example.demo.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderSide;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.model.Asset;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.OrderRepository;
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
    private AuthService authService;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO validOrderDTO;

    private Asset tryAsset;

    private String authHeader;

    @BeforeEach
    public void setUp() throws AccessDeniedException {
        authHeader = "Bearer 12345abc";
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
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);

        String result = orderService.createOrder(authHeader, validOrderDTO);

        assertEquals("Order created successfully!", result);
        verify(assetRepository).save(any(Asset.class));
    }

    @Test
    public void givenInsufficientTRYBalance_whenCreateOrder_thenThrowInsufficientBalanceException() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(tryAsset);
        validOrderDTO = validOrderDTO.toBuilder().size(200).build();

        Exception exception = assertThrows(InsufficientBalanceException.class, () -> {
            orderService.createOrder(authHeader, validOrderDTO);
        });

        assertEquals("Insufficient TRY!", exception.getMessage());
    }

    @Test
    public void givenInsufficientSellAssetBalance_whenCreateOrder_thenReturnInsufficientBalanceMessage() {
       Asset sellAsset = Asset.builder().customerId(1L).assetName("BTC").usableSize(50).build();
        when(assetRepository.findByCustomerIdAndAssetName(1L, "BTC")).thenReturn(sellAsset);
        validOrderDTO = validOrderDTO.toBuilder()
                .orderSide(OrderSide.SELL)
                .size(100)
                .build();

        String result = orderService.createOrder(authHeader, validOrderDTO);
        assertEquals("Insufficient asset!", result);
    }
}
