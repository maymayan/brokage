package com.maymayan.brokage.config;

import com.maymayan.brokage.entity.Asset;
import com.maymayan.brokage.entity.Customer;
import com.maymayan.brokage.repository.AssetRepository;
import com.maymayan.brokage.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DatabaseConfig {

    @Bean
    CommandLineRunner initDatabase(CustomerRepository customerRepository, AssetRepository assetRepository) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return args -> {
            customerRepository.save(Customer.builder().name("Jane").surname("Doe").username("janedoe").password(encoder.encode("123")).role("admin").build());
            customerRepository.save(Customer.builder().name("John").surname("Doe").username("johndoe").password(encoder.encode("123")).role("user").build());
            var asset = new Asset();
            asset.setCustomerId(customerRepository.findByUsername("janedoe").get().getId());
            asset.setAssetSize(BigDecimal.valueOf(10000));
            asset.setAssetName("TRY");
            asset.setUsableSize(BigDecimal.valueOf(10000));
            var asset2 = new Asset();
            asset2.setCustomerId(customerRepository.findByUsername("johndoe").get().getId());
            asset2.setAssetSize(BigDecimal.valueOf(10000));
            asset2.setAssetName("TRY");
            asset2.setUsableSize(BigDecimal.valueOf(10000));
            assetRepository.save(asset);
            assetRepository.save(asset2);
        };
    }
}

