package com.restaurant.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor // Lombok necesita este constructor vacío para deserialización
@AllArgsConstructor // Constructor con todos los campos
public class Product {
    private UUID id;
    private String name;
    private Double price;
    private String category;
}
