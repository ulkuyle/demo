package com.example.demo.service;

import com.example.demo.model.Asset;
import com.example.demo.model.Customer;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AssetRepository assetRepository;

    public Optional<Customer> findByCustomerId(Long customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        String hashedPassword = passwordEncoder.encode(customer.getPassword());
        Customer newCustomer = Customer.builder()
                .name(customer.getName()).customerId(customer.getCustomerId())
                .password(hashedPassword).role(customer.getRole())
                .build();
        // create 1000 TRY for new customer
        Asset asset = Asset.builder().assetName("TRY").customerId(customer.getCustomerId()).size(1000).usableSize(1000).build();
        assetRepository.save(asset);
        return customerRepository.save(newCustomer);
    }
}
