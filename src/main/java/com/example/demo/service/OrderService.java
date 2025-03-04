package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderSide;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.model.Asset;
import com.example.demo.model.AuthToken;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    private final AuthService authService;

    @SneakyThrows
    public String createOrder(String authorizationHeader, OrderDTO order) {
        authService.checkIfUserAuthorized(authorizationHeader, order.getCustomerId());

        if (order.getOrderSide() == OrderSide.BUY) {
            Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY");

            BigDecimal requiredAmount = order.getPrice().multiply(BigDecimal.valueOf(order.getSize()));
            if(tryAsset == null || BigDecimal.valueOf(tryAsset.getUsableSize()).compareTo(requiredAmount) < 0) {
                throw new InsufficientBalanceException("Insufficient TRY!");
            }

            tryAsset = tryAsset.toBuilder()
                    .usableSize(BigDecimal.valueOf(tryAsset.getUsableSize()).subtract(requiredAmount).intValue())
                    .build();
            assetRepository.save(tryAsset);

        } else {
            Asset sellAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());

            if (sellAsset == null || BigDecimal.valueOf(sellAsset.getUsableSize()).compareTo(BigDecimal.valueOf(order.getSize())) < 0) {
                return "Insufficient asset!";
            }

            sellAsset = sellAsset.toBuilder()
                    .usableSize(BigDecimal.valueOf(sellAsset.getUsableSize()).subtract(BigDecimal.valueOf(order.getSize())).intValue())
                    .build();
            assetRepository.save(sellAsset);
        }

        Order createdOrder = Order.builder().customerId(order.getCustomerId()).assetName(order.getAssetName()).orderSide(order.getOrderSide()).size(order.getSize()).price(order.getPrice())
                .orderStatus(OrderStatus.PENDING).createdDate(LocalDateTime.now()).build();
        orderRepository.save(createdOrder);

        return "Order created successfully!";

    }

    public void matchPendingOrders(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdAndOrderStatus(customerId, OrderStatus.PENDING);
        orders.forEach(order -> {
            Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());

            if (asset == null) {
                asset = Asset.builder()
                        .assetName(order.getAssetName())
                        .usableSize(0)
                        .customerId(order.getCustomerId())
                        .size(0)
                        .build();
            }

            if (order.getOrderSide() == OrderSide.BUY) {
                asset = asset.toBuilder()
                        .size(asset.getSize() + order.getSize())
                        .usableSize(asset.getUsableSize() + (order.getSize()))
                        .build();

                Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY");
                tryAsset = tryAsset.toBuilder()
                        .size(tryAsset.getUsableSize())
                        .build();
                assetRepository.save(tryAsset);
            } else {
                Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY");

                if (tryAsset != null) {
                    Asset updatedTryAsset = tryAsset.toBuilder()
                            .size(BigDecimal.valueOf(tryAsset.getSize()).add(BigDecimal.valueOf(order.getSize()).multiply(order.getPrice())).intValue())
                            .usableSize(tryAsset.getUsableSize() + BigDecimal.valueOf(order.getSize()).multiply(order.getPrice()).intValue())
                            .build();
                    assetRepository.save(updatedTryAsset);
                }
                asset = asset.toBuilder()
                        .size(asset.getSize() - order.getSize()).build();
            }

            assetRepository.save(asset);
            orderRepository.save(order.toBuilder().orderStatus(OrderStatus.MATCHED).build());
        });

    }

    @SneakyThrows
    public List<Order> getOrdersByCustomer(String authorizationHeader, Long customerId)  {
        authService.checkIfUserAuthorized(authorizationHeader, customerId);
        return orderRepository.findByCustomerId(customerId);
    }

    @SneakyThrows
    public String deleteOrder(String authorizationHeader, Long orderId, Long customerId) {
        authService.checkIfUserAuthorized(authorizationHeader, customerId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be deleted");
        }

        if (order.getOrderSide() == OrderSide.BUY) {
            updateTryAsset(order);
        } else {
            updateSellAsset(order);
        }

        orderRepository.delete(order);
        return "Order deleted successfully!";
    }

    private void updateSellAsset(Order order) {

        Asset sellAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());

        if (sellAsset != null) {
            Asset updatedSellAsset = sellAsset.toBuilder()
                    .usableSize(sellAsset.getUsableSize() + order.getSize())
                    .build();
            assetRepository.save(updatedSellAsset);
        }
    }

    private void updateTryAsset(Order order) {
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY");

        if (tryAsset != null) {
            Asset updatedTryAsset = tryAsset.toBuilder()
                    .usableSize(tryAsset.getUsableSize() + order.getPrice().multiply(BigDecimal.valueOf(order.getSize())).intValue())
                    .build();
            assetRepository.save(updatedTryAsset);
        }
    }
}
