package com.maymayan.brokage.dto;

import com.maymayan.brokage.entity.EnumOrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderQueryModel {

    private Long customerId;

    private LocalDateTime beginDate;

    private LocalDateTime endDate;

    private String assetName;

    private BigDecimal minSize;

    private BigDecimal maxSize;

    private EnumOrderStatus status;

}
