package com.example.demo.service.strategy;

import com.example.demo.dto.OrderDTO;
import com.example.demo.enums.OrderSide;
import com.example.demo.exception.InsufficientAssetException;
import com.example.demo.model.Asset;
import com.example.demo.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SellOrder implements OrderSideHandling {

    private final AssetRepository assetRepository;

    @Override
    public void handleOrder(OrderDTO order) throws InsufficientAssetException {
        Asset sellAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());

        if (sellAsset == null || BigDecimal.valueOf(sellAsset.getUsableSize()).compareTo(BigDecimal.valueOf(order.getSize())) < 0) {
            throw new InsufficientAssetException("Insufficient asset!");
        }

        sellAsset = sellAsset.toBuilder()
                .usableSize(BigDecimal.valueOf(sellAsset.getUsableSize()).subtract(BigDecimal.valueOf(order.getSize())).intValue())
                .build();
        assetRepository.save(sellAsset);
    }

    @Override
    public OrderSide getOrderSide() {
        return OrderSide.SELL;
    }
}
