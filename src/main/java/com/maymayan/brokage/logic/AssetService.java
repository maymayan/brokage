package com.maymayan.brokage.logic;

import com.maymayan.brokage.dto.AssetQueryModel;
import com.maymayan.brokage.entity.Asset;
import com.maymayan.brokage.repository.AssetRepository;
import com.maymayan.brokage.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AssetService {
    private final AssetRepository repository;
    private final CustomerRepository customerRepository;

    public AssetService(AssetRepository repository,
                        CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public List<Asset> list(AssetQueryModel queryModel) {
        queryModel.setCustomerId(getCustomerIdIfNotAdmin());
        return repository.findAll(AssetSpecification.filter(queryModel));
    }

    public Asset getOrCreate(Long customerId, String assetName) {
        return repository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> repository.save(new Asset(customerId, assetName, BigDecimal.ZERO, BigDecimal.ZERO)));
    }

    @Transactional
    public void increase(Long customerId, String asset, BigDecimal delta, boolean usableToo) {
        Asset a = getOrCreate(customerId, asset);
        a.setAssetSize(a.getAssetSize().add(delta));
        if (usableToo) {
            a.setUsableSize(a.getUsableSize().add(delta));
        }
        repository.save(a);
    }

    @Transactional
    public void decrease(Long customerId, String asset, BigDecimal using) {
        Asset a = getOrCreate(customerId, asset);
        if (a.getAssetSize().compareTo(using) < 0) {
            throw new RuntimeException("Insufficient total size");
        }
        a.setAssetSize(a.getAssetSize().subtract(using));
        repository.save(a);
    }

    @Transactional
    public void adjustUsable(Long customerId, String asset, BigDecimal using) {
        Asset a = getOrCreate(customerId, asset);
        BigDecimal newUsable = a.getUsableSize().add(using);
        if (newUsable.compareTo(BigDecimal.ZERO) < 0) throw new RuntimeException("Insufficient usable");
        if (newUsable.compareTo(a.getAssetSize()) > 0) a.setAssetSize(newUsable);
        a.setUsableSize(newUsable);
        repository.save(a);
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
