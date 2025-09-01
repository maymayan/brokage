package com.maymayan.brokage.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String assetName;

    @PositiveOrZero
    private BigDecimal assetSize;

    @PositiveOrZero
    private BigDecimal usableSize;

    public Asset() {
    }

    public Asset(Long customerId, String assetName, BigDecimal assetSize, BigDecimal usableSize) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.assetSize = assetSize;
        this.usableSize = usableSize;
    }


}
