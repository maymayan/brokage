package com.maymayan.brokage.logic;

import com.maymayan.brokage.dto.OrderQueryModel;
import com.maymayan.brokage.dto.OrderSaveModel;
import com.maymayan.brokage.entity.EnumOrderSide;
import com.maymayan.brokage.entity.EnumOrderStatus;
import com.maymayan.brokage.entity.Order;
import com.maymayan.brokage.mapper.OrderMapper;
import com.maymayan.brokage.repository.CustomerRepository;
import com.maymayan.brokage.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private AssetService assetService;
    private CustomerRepository customerRepository;
    private OrderMapper mapper;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        assetService = mock(AssetService.class);
        customerRepository = mock(CustomerRepository.class);
        mapper = mock(OrderMapper.class);

        orderService = new OrderService(orderRepository, assetService, customerRepository, mapper) {
            @Override
            protected Long getCustomerIdIfNotAdmin() {
                return 1L;
            }
        };
    }

    @Test
    void testCreateBuyOrder_adjustsUsable() {
        OrderSaveModel saveModel = new OrderSaveModel();
        saveModel.setCustomerId(1L);
        saveModel.setOrderSide(EnumOrderSide.BUY);
        saveModel.setAssetName("AAPL");
        saveModel.setOrderSize(BigDecimal.TEN);
        saveModel.setPrice(BigDecimal.valueOf(5));

        Order orderEntity = new Order();
        when(mapper.convertToEntity(saveModel)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        Order result = orderService.create(saveModel);

        assertEquals(EnumOrderStatus.PENDING, result.getOrderStatus());
        assertNotNull(result.getCreateDate());
        verify(assetService).adjustUsable(eq(1L), eq("TRY"), eq(BigDecimal.valueOf(50).negate()));
    }

    @Test
    void testCreateSellOrder_adjustsUsable() {
        OrderSaveModel saveModel = new OrderSaveModel();
        saveModel.setCustomerId(1L);
        saveModel.setOrderSide(EnumOrderSide.SELL);
        saveModel.setAssetName("AAPL");
        saveModel.setOrderSize(BigDecimal.TEN);
        saveModel.setPrice(BigDecimal.valueOf(5));

        Order orderEntity = new Order();
        when(mapper.convertToEntity(saveModel)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        Order result = orderService.create(saveModel);

        assertEquals(EnumOrderStatus.PENDING, result.getOrderStatus());
        assertNotNull(result.getCreateDate());
        verify(assetService).adjustUsable(eq(1L), eq("AAPL"), eq(BigDecimal.TEN.negate()));
    }

    @Test
    void testMatchBuyOrder_success() {
        Order pendingBuyOrder = new Order();
        pendingBuyOrder.setId(1L);
        pendingBuyOrder.setCustomerId(1L);
        pendingBuyOrder.setOrderSide(EnumOrderSide.BUY);
        pendingBuyOrder.setOrderStatus(EnumOrderStatus.PENDING);
        pendingBuyOrder.setOrderSize(BigDecimal.TEN);
        pendingBuyOrder.setPrice(BigDecimal.valueOf(5));
        pendingBuyOrder.setAssetName("AAPL");

        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingBuyOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.match(1L);

        assertEquals(EnumOrderStatus.MATCHED, pendingBuyOrder.getOrderStatus());
        verify(assetService).decrease(1L, "TRY", BigDecimal.valueOf(50));
        verify(assetService).increase(1L, "AAPL", BigDecimal.TEN, true);
        verify(orderRepository).save(pendingBuyOrder);
    }

    @Test
    void testMatchSellOrder_success() {
        Order pendingSellOrder = new Order();
        pendingSellOrder.setId(2L);
        pendingSellOrder.setCustomerId(1L);
        pendingSellOrder.setOrderSide(EnumOrderSide.SELL);
        pendingSellOrder.setOrderStatus(EnumOrderStatus.PENDING);
        pendingSellOrder.setOrderSize(BigDecimal.valueOf(8));
        pendingSellOrder.setPrice(BigDecimal.valueOf(12));
        pendingSellOrder.setAssetName("AAPL");

        when(orderRepository.findById(2L)).thenReturn(java.util.Optional.of(pendingSellOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.match(2L);

        assertEquals(EnumOrderStatus.MATCHED, pendingSellOrder.getOrderStatus());
        verify(assetService).decrease(1L, "AAPL", BigDecimal.valueOf(8));
        verify(assetService).increase(1L, "TRY", BigDecimal.valueOf(96), true);
        verify(orderRepository).save(pendingSellOrder);
    }

    @Test
    void testMatchNonPendingOrder_throwsException() {
        Order matchedOrder = new Order();
        matchedOrder.setId(3L);
        matchedOrder.setOrderStatus(EnumOrderStatus.MATCHED);
        when(orderRepository.findById(3L)).thenReturn(java.util.Optional.of(matchedOrder));
        assertThrows(IllegalStateException.class, () -> orderService.match(3L));
    }

    @Test
    void testDeletePendingBuyOrder_asOwner_success() {
        Order pendingBuyOrder = new Order();
        pendingBuyOrder.setId(1L);
        pendingBuyOrder.setCustomerId(1L);
        pendingBuyOrder.setOrderSide(EnumOrderSide.BUY);
        pendingBuyOrder.setOrderStatus(EnumOrderStatus.PENDING);
        pendingBuyOrder.setOrderSize(BigDecimal.TEN);
        pendingBuyOrder.setPrice(BigDecimal.valueOf(5));

        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingBuyOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.deleteById(1L);

        assertEquals(EnumOrderStatus.CANCELED, pendingBuyOrder.getOrderStatus());
        verify(assetService).adjustUsable(1L, "TRY", BigDecimal.valueOf(50));
        verify(orderRepository).save(pendingBuyOrder);
    }

    @Test
    void testDeletePendingSellOrder_asOwner_success() {
        Order pendingSellOrder = new Order();
        pendingSellOrder.setId(1L);
        pendingSellOrder.setCustomerId(1L);
        pendingSellOrder.setAssetName("TRY");
        pendingSellOrder.setOrderSide(EnumOrderSide.SELL);
        pendingSellOrder.setOrderStatus(EnumOrderStatus.PENDING);
        pendingSellOrder.setOrderSize(BigDecimal.TEN);
        pendingSellOrder.setPrice(BigDecimal.valueOf(5));

        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingSellOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.deleteById(1L);

        assertEquals(EnumOrderStatus.CANCELED, pendingSellOrder.getOrderStatus());

        verify(assetService).adjustUsable(1L, "TRY", BigDecimal.TEN);
        verify(orderRepository).save(pendingSellOrder);
    }

    @Test
    void testDeleteNonPendingOrder_throwsException() {
        Order matchedOrder = new Order();
        matchedOrder.setId(2L);
        matchedOrder.setOrderStatus(EnumOrderStatus.MATCHED);

        when(orderRepository.findById(2L)).thenReturn(java.util.Optional.of(matchedOrder));

        assertThrows(RuntimeException.class, () -> orderService.deleteById(2L));
    }

    @Test
    void testDeleteOrder_asDifferentUser_throwsAuthorizationDeniedException() {
        Order pendingOrder = new Order();
        pendingOrder.setId(3L);
        pendingOrder.setCustomerId(99L);
        pendingOrder.setOrderStatus(EnumOrderStatus.PENDING);

        when(orderRepository.findById(3L)).thenReturn(java.util.Optional.of(pendingOrder));

        assertThrows(org.springframework.security.authorization.AuthorizationDeniedException.class,
                () -> orderService.deleteById(3L));
    }

    @Test
    void testListOrders_asNormalUser_filtersByCustomerId() {
        OrderQueryModel query = new OrderQueryModel();
        query.setCustomerId(null);

        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(new Order(), new Order()));

        var result = orderService.list(query);

        assertEquals(2, result.size());
        verify(orderRepository).findAll(any(Specification.class));
    }

    @Test
    void testListOrders_asAdmin_returnsAll() {
        OrderService adminService = new OrderService(orderRepository, assetService, customerRepository, mapper) {
            @Override
            protected Long getCustomerIdIfNotAdmin() {
                return null;
            }
        };

        OrderQueryModel query = new OrderQueryModel();

        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(new Order(), new Order(), new Order()));

        var result = adminService.list(query);

        assertEquals(3, result.size());
        verify(orderRepository).findAll(any(Specification.class));
    }


}
