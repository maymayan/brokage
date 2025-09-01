package com.maymayan.brokage.controller;

import com.maymayan.brokage.dto.AssetQueryModel;
import com.maymayan.brokage.entity.Asset;
import com.maymayan.brokage.logic.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<List<Asset>> list(@RequestBody AssetQueryModel queryModel) {
        return ResponseEntity.ok(assetService.list(queryModel));
    }
}