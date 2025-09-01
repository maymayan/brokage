package com.maymayan.brokage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String assetName;

    private EnumOrderSide orderSide;

    @Positive
    private BigDecimal orderSize;

    @Positive
    private BigDecimal price;

    private EnumOrderStatus orderStatus;

    private LocalDateTime createDate;

    public Order() {
    }

    public Order(Long customerId, String assetName, EnumOrderSide orderSide, BigDecimal orderSize, BigDecimal price) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.orderSize = orderSize;
        this.price = price;
    }
}
