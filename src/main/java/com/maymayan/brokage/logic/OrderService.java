package com.maymayan.brokage.logic;

import com.maymayan.brokage.dto.OrderQueryModel;
import com.maymayan.brokage.dto.OrderSaveModel;
import com.maymayan.brokage.entity.EnumOrderSide;
import com.maymayan.brokage.entity.EnumOrderStatus;
import com.maymayan.brokage.entity.Order;
import com.maymayan.brokage.mapper.OrderMapper;
import com.maymayan.brokage.repository.CustomerRepository;
import com.maymayan.brokage.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final AssetService assetService;
    private final CustomerRepository customerRepository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository repository, AssetService assetService,
                        CustomerRepository customerRepository, OrderMapper mapper) {
        this.repository = repository;
        this.assetService = assetService;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Order create(OrderSaveModel order) {
        var customerIdIfNotAdmin = getCustomerIdIfNotAdmin();
        if (customerIdIfNotAdmin != null && !customerIdIfNotAdmin.equals(order.getCustomerId())) {
            throw new AuthorizationDeniedException("only admins can manipulate other customer's orders");
        }
        if (order.getOrderSide() == EnumOrderSide.BUY) {
            assetService.adjustUsable(order.getCustomerId(), "TRY", order.getPrice().multiply(order.getOrderSize()).negate());
        } else {
            assetService.adjustUsable(order.getCustomerId(), order.getAssetName(), order.getOrderSize().negate());
        }
        var entity = mapper.convertToEntity(order);
        entity.setOrderStatus(EnumOrderStatus.PENDING);
        entity.setCreateDate(LocalDateTime.now());
        return repository.save(entity);
    }

    public List<Order> list(OrderQueryModel queryModel) {
        queryModel.setCustomerId(getCustomerIdIfNotAdmin());
        return repository.findAll(OrderSpecification.filter(queryModel));
    }

    @Transactional
    public void deleteById(Long orderId) {
        Order o = repository.findById(orderId).orElseThrow();
        var customerIdIfNotAdmin = getCustomerIdIfNotAdmin();
        if (customerIdIfNotAdmin != null && !customerIdIfNotAdmin.equals(o.getCustomerId())) {
            throw new AuthorizationDeniedException("only admins can manipulate other customer's orders");
        }
        if (o.getOrderStatus() != EnumOrderStatus.PENDING)
            throw new RuntimeException("Orders not in pending status can't be canceled");
        if (o.getOrderSide() == EnumOrderSide.BUY) {
            assetService.adjustUsable(o.getCustomerId(), "TRY", o.getPrice().multiply(o.getOrderSize()));
        } else {
            assetService.adjustUsable(o.getCustomerId(), o.getAssetName(), o.getOrderSize());
        }
        o.setOrderStatus(EnumOrderStatus.CANCELED);
        repository.save(o);
    }

    @Transactional
    public void match(Long orderId) {
        Order o = repository.findById(orderId).orElseThrow();
        if (o.getOrderStatus() != EnumOrderStatus.PENDING)
            throw new IllegalStateException("Orders not in pending status can't be matched");

        if (o.getOrderSide() == EnumOrderSide.BUY) {
            assetService.decrease(o.getCustomerId(), "TRY", o.getPrice().multiply(o.getOrderSize()));
            assetService.increase(o.getCustomerId(), o.getAssetName(), o.getOrderSize(), true);
        } else {
            assetService.decrease(o.getCustomerId(), o.getAssetName(), o.getOrderSize());
            assetService.increase(o.getCustomerId(), "TRY", o.getPrice().multiply(o.getOrderSize()), true);
        }
        o.setOrderStatus(EnumOrderStatus.MATCHED);
        repository.save(o);
    }


    Long getCustomerIdIfNotAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin"))) {
            return null;
        } else {
            return customerRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found")).getId();
        }
    }
}
