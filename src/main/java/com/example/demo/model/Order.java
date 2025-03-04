package com.example.demo.model;

import com.example.demo.enums.OrderSide;
import com.example.demo.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String assetName;
    private int size;
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;
    private BigDecimal price;
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
