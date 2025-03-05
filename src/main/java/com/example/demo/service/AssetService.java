package com.example.demo.service;

import com.example.demo.model.Asset;
import com.example.demo.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    public List<Asset> getAssetsByCustomer(Long customerId) {
        return Optional.ofNullable(assetRepository.findByCustomerId(customerId))
                .filter(assets -> !assets.isEmpty())
                .orElseThrow(() -> new NoSuchElementException("No asset found for customer: " + customerId));
    }
}
