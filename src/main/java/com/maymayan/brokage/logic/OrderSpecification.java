package com.maymayan.brokage.logic;

import com.maymayan.brokage.dto.OrderQueryModel;
import com.maymayan.brokage.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> filter(OrderQueryModel queryModel) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (queryModel.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), queryModel.getCustomerId()));
            }
            if (queryModel.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryModel.getStatus()));
            }
            if (queryModel.getAssetName() != null) {
                predicates.add(cb.like(root.get("assetName"), queryModel.getAssetName()));
            }
            if (queryModel.getMinSize() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("size"), queryModel.getMinSize()));
            }

            if (queryModel.getMaxSize() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("size"), queryModel.getMaxSize()));
            }

            if (queryModel.getBeginDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createDate"), queryModel.getBeginDate()));
            }
            if (queryModel.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createDate"), queryModel.getEndDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
