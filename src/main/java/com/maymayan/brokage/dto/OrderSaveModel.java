package com.maymayan.brokage.dto;

import com.maymayan.brokage.entity.EnumOrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSaveModel {
    @NotNull
    private Long customerId;
    @NotBlank
    private String assetName;
    @NotNull
    private EnumOrderSide orderSide;
    @NotNull
    @Positive
    private BigDecimal orderSize;
    @NotNull
    @Positive
    private BigDecimal price;
}
