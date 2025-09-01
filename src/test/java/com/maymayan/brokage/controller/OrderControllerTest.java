package com.maymayan.brokage.controller;

import com.maymayan.brokage.dto.OrderQueryModel;
import com.maymayan.brokage.dto.OrderSaveModel;
import com.maymayan.brokage.entity.EnumOrderSide;
import com.maymayan.brokage.entity.Order;
import com.maymayan.brokage.logic.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
    }

    @Test
    void testCreateOrder() {
        OrderSaveModel saveModel = new OrderSaveModel();
        saveModel.setCustomerId(1L);
        saveModel.setOrderSide(EnumOrderSide.BUY);
        saveModel.setAssetName("AAPL");
        saveModel.setOrderSize(BigDecimal.TEN);
        saveModel.setPrice(BigDecimal.valueOf(5));

        Order order = new Order();
        when(orderService.create(saveModel)).thenReturn(order);
        ResponseEntity<Order> response = orderController.create(saveModel);
        assertEquals(order, response.getBody());
        verify(orderService).create(saveModel);
    }

    @Test
    void testListOrders() {
        OrderQueryModel queryModel = new OrderQueryModel();

        Order order = new Order();
        when(orderService.list(queryModel)).thenReturn(List.of(order));
        ResponseEntity<List<Order>> response = orderController.list(queryModel);
        assertEquals(List.of(order), response.getBody());
        verify(orderService).list(queryModel);
    }

    @Test
    void testCancelOrder() {
        orderController.cancel(1L);
        verify(orderService).deleteById(1L);
    }

    @Test
    void testMatchOrder() {
        orderController.match(1L);
        verify(orderService).match(1L);
    }
}
