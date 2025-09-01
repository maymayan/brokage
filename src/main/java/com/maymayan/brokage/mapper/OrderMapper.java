package com.maymayan.brokage.mapper;

import com.maymayan.brokage.dto.OrderSaveModel;
import com.maymayan.brokage.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order convertToEntity(OrderSaveModel model) {
        if (model == null) return null;
        return new Order(model.getCustomerId(), model.getAssetName(), model.getOrderSide(), model.getOrderSize(), model.getPrice());
    }

}
