package com.maymayan.brokage.controller;

import com.maymayan.brokage.dto.OrderQueryModel;
import com.maymayan.brokage.dto.OrderSaveModel;
import com.maymayan.brokage.entity.Order;
import com.maymayan.brokage.logic.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> create(@Valid @RequestBody OrderSaveModel order) {
        var o = orderService.create(order);
        return ResponseEntity.ok(o);
    }

    @PostMapping("/list")
    public ResponseEntity<List<Order>> list(@RequestBody OrderQueryModel queryModel) {
        return ResponseEntity.ok(orderService.list(queryModel));
    }

    @DeleteMapping("/cancelBy/{id}")
    public void cancel(@PathVariable Long id) {
        orderService.deleteById(id);
    }

    @PreAuthorize("hasRole('admin')")
    @PatchMapping("/matchBy/{id}")
    public void match(@PathVariable Long id) {
        orderService.match(id);
    }

}
