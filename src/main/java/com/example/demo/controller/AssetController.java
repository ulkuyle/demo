package com.example.demo.controller;

import com.example.demo.model.Asset;
import com.example.demo.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Management", description = "Operations related to assets")
public class AssetController {

    private final AssetService assetService;

    @GetMapping(value = "/list")
    @Operation(summary = "Get a list of assets for a specific customer")
    public List<Asset> listAssets(@RequestParam Long customerId) {
        return assetService.getAssetsByCustomer(customerId);
    }

}
