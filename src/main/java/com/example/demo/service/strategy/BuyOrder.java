package com.example.demo.service.strategy;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderSide;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.model.Asset;
import com.example.demo.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BuyOrder implements OrderSideHandling {

    private final AssetRepository assetRepository;

    @Override
    public void handleOrder(OrderDTO order) throws InsufficientBalanceException{
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY");

        BigDecimal requiredAmount = order.getPrice().multiply(BigDecimal.valueOf(order.getSize()));
        if (tryAsset == null || BigDecimal.valueOf(tryAsset.getUsableSize()).compareTo(requiredAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient TRY balance!");
        }

        tryAsset = tryAsset.toBuilder()
                .usableSize(BigDecimal.valueOf(tryAsset.getUsableSize()).subtract(requiredAmount).intValue())
                .build();
        assetRepository.save(tryAsset);
    }

    @Override
    public OrderSide getOrderSide() {
        return OrderSide.BUY;
    }
}
