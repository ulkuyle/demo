package com.example.demo.service;

import com.example.demo.model.AuthToken;
import com.example.demo.model.Customer;
import com.example.demo.repository.AuthTokenRepository;
import com.example.demo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private CustomerRepository customerRepository;

    private AuthTokenRepository authTokenRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public UUID login(String username, String password) {
        var customer = customerRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (passwordEncoder.matches(password, customer.getPassword())) {
            UUID token = UUID.randomUUID();

            AuthToken authToken = AuthToken.builder()
                    .token(token)
                    .customer(customer)
                    .build();
            authTokenRepository.save(authToken);
            return token;
        }
        throw new RuntimeException("Invalid username or password");
    }

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public AuthToken authenticate(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token != null) {
            try {
                UUID uuid = UUID.fromString(token);
                return authTokenRepository.findByToken(uuid).orElse(null);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public void  checkIfUserAuthorized(String authorizationHeader, Long customedId) throws AccessDeniedException {
        AuthToken authToken = authenticate(authorizationHeader);
        if (authToken == null) {
            throw new AccessDeniedException("Authentication failed. Please check your credentials.");
        }

        Customer authenticatedCustomer = authToken.getCustomer();
        if (!authenticatedCustomer.getCustomerId().equals(customedId) &&
                authenticatedCustomer.getRole().equals("ROLE_USER")) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }
    }

}
