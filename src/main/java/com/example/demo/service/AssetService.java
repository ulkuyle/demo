package com.example.demo.service;

import com.example.demo.model.Asset;
import com.example.demo.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    public List<Asset> getAssetsByCustomer(Long customerId) {
        List<Asset> assets = assetRepository.findByCustomerId(customerId);
        if (assets.isEmpty()) {
            throw new NoSuchElementException("No asset found for customer: " + customerId);
        }
        return assets;
    }
}
