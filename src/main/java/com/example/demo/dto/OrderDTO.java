package com.example.demo.dto;

import com.example.demo.enums.OrderSide;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderDTO {
    @NotNull(message = "Customer ID cannot be null")
    @Positive(message = "Customer ID must be a positive number.")
    private Long customerId;

    @NotNull(message = "Asset name cannot be null")
    @Size(max = 5, message = "Asset name must be at most 5 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,5}$", message = "Asset name can only contain letters and numbers and at most 5 characters.")
    private String assetName;

    @NotNull(message = "Size cannot be null")
    @Min(value = 1, message = "Size must be at least 1")
    private int size;

    @NotNull(message = "Order Side cannot be null")
    private OrderSide orderSide;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive number")
    private BigDecimal price;

}
