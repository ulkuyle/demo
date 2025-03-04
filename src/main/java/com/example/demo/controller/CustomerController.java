package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Operations related to customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(value ="/find")
    public ResponseEntity<Customer> findCustomer(@RequestBody Long customerId) {
        Customer createdCustomer = customerService.findByCustomerId(customerId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No such Customer: " + customerId
        ));
        return ResponseEntity.ok(createdCustomer);
    }

    @Operation(description = "This endpoint creates a new customer")
    @PostMapping("/create")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }
}
