package com.maymayan.brokage.controller;

import com.maymayan.brokage.dto.AssetQueryModel;
import com.maymayan.brokage.entity.Asset;
import com.maymayan.brokage.logic.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AssetControllerTest {

    private AssetService assetService;
    private AssetController assetController;

    @BeforeEach
    void setUp() {
        assetService = mock(AssetService.class);
        assetController = new AssetController(assetService);
    }

    @Test
    void testListAssets() {
        AssetQueryModel query = new AssetQueryModel();
        List<Asset> assets = List.of(new Asset(1L, "TRY", BigDecimal.TEN, BigDecimal.TEN));
        when(assetService.list(query)).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.list(query);
        assertEquals(1, response.getBody().size());
        verify(assetService).list(query);
    }
}
