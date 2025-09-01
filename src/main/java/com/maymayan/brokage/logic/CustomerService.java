package com.maymayan.brokage.logic;

import com.maymayan.brokage.entity.Customer;
import com.maymayan.brokage.repository.CustomerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer user = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public Customer createCustomer(Customer customer) {
        if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        } else {
            return customerRepository.save(customer);
        }
    }

}
