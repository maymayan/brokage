package com.maymayan.brokage.logic;

import com.maymayan.brokage.dto.AssetQueryModel;
import com.maymayan.brokage.entity.Asset;
import com.maymayan.brokage.repository.AssetRepository;
import com.maymayan.brokage.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    private AssetRepository assetRepository;
    private CustomerRepository customerRepository;
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        assetRepository = mock(AssetRepository.class);
        customerRepository = mock(CustomerRepository.class);
        assetService = new AssetService(assetRepository, customerRepository) {
            @Override
            Long getCustomerIdIfNotAdmin() {
                return 1L;
            }
        };
    }

    @Test
    void testGetOrCreate_existingAsset() {
        Asset asset = new Asset(1L, "TRY", BigDecimal.TEN, BigDecimal.TEN);
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(Optional.of(asset));

        Asset result = assetService.getOrCreate(1L, "TRY");
        assertEquals(asset, result);
        verify(assetRepository, never()).save(any());
    }

    @Test
    void testGetOrCreate_newAsset() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(Optional.empty());
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Asset result = assetService.getOrCreate(1L, "TRY");
        assertEquals("TRY", result.getAssetName());
        assertEquals(BigDecimal.ZERO, result.getAssetSize());
        assertEquals(BigDecimal.ZERO, result.getUsableSize());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testIncrease() {
        Asset asset = new Asset(1L, "TRY", BigDecimal.TEN, BigDecimal.TEN);
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(Optional.of(asset));
        when(assetRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assetService.increase(1L, "TRY", BigDecimal.valueOf(5), true);
        assertEquals(BigDecimal.valueOf(15), asset.getAssetSize());
        assertEquals(BigDecimal.valueOf(15), asset.getUsableSize());
    }

    @Test
    void testList_withCustomerIdAndAssetName() {
        AssetQueryModel query = new AssetQueryModel();
        query.setCustomerId(1L);
        query.setAssetName("TRY");

        Asset asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetName("TRY");

        when(assetRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(asset));

        var result = assetService.list(query);

        assertEquals(1, result.size());
        assertEquals("TRY", result.get(0).getAssetName());
        verify(assetRepository).findAll(any(Specification.class));
    }

    @Test
    void testDecrease_success() {
        Asset asset = new Asset();
        asset.setId(10L);
        asset.setCustomerId(1L);
        asset.setAssetName("TRY");
        asset.setAssetSize(BigDecimal.valueOf(100));
        asset.setUsableSize(BigDecimal.valueOf(80));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assetService.decrease(1L, "TRY", BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), asset.getAssetSize());
        verify(assetRepository).save(asset);
    }

    @Test
    void testDecrease_notEnoughBalance_throwsException() {
        Asset asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetName("TRY");
        asset.setAssetSize(BigDecimal.valueOf(20));
        asset.setUsableSize(BigDecimal.valueOf(10));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(asset));

        assertThrows(RuntimeException.class,
                () -> assetService.decrease(1L, "TRY", BigDecimal.valueOf(50)));
    }

    @Test
    void testAdjustUsable_increaseSuccess() {
        Asset asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetName("TRY");
        asset.setAssetSize(BigDecimal.valueOf(100));
        asset.setUsableSize(BigDecimal.valueOf(50));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assetService.adjustUsable(1L, "TRY", BigDecimal.valueOf(20));

        assertEquals(BigDecimal.valueOf(70), asset.getUsableSize());
    }

    @Test
    void testAdjustUsable_decreaseBelowZero_throwsException() {
        Asset asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetName("TRY");
        asset.setAssetSize(BigDecimal.valueOf(100));
        asset.setUsableSize(BigDecimal.valueOf(10));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(asset));

        assertThrows(RuntimeException.class,
                () -> assetService.adjustUsable(1L, "TRY", BigDecimal.valueOf(-50)));
    }
}
