package com.example.demo.service;

import com.example.demo.model.Asset;
import com.example.demo.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.NoSuchElementException;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    @Test
    void givenExistingCustomerId_whenGetAssetsByCustomer_thenReturnAssetList() {
        List<Asset> assets = List.of(new Asset(), new Asset());
        when(assetRepository.findByCustomerId(1L)).thenReturn(assets);
        List<Asset> result = assetService.getAssetsByCustomer(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void givenNonExistingCustomerId_whenGetAssetsByCustomer_thenThrowNoSuchElementException() {
        when(assetRepository.findByCustomerId(2L)).thenReturn(List.of());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                assetService.getAssetsByCustomer(2L)
        );
        assertEquals("No asset found for customer: " + 2L, exception.getMessage());
    }
}
