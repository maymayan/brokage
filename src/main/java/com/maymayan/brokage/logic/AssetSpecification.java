package com.maymayan.brokage.logic;

import com.maymayan.brokage.dto.AssetQueryModel;
import com.maymayan.brokage.entity.Asset;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AssetSpecification {

    public static Specification<Asset> filter(AssetQueryModel queryModel) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (queryModel.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), queryModel.getCustomerId()));
            }
            if (queryModel.getAssetName() != null) {
                predicates.add(cb.like(root.get("assetName"), queryModel.getAssetName()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
