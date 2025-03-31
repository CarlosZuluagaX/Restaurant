package com.restaurant.domain.model;

import lombok.Getter;

@Getter
public class Coupon {
    // Método para obtener el código del cupón
    private final String code;
    // Método para obtener el valor fijo del descuento
    private final double discountValue;
    // Método para obtener el porcentaje del descuento
    private final double discountPercent;

    public Coupon(String code, double discountValue, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        this.code = code;
        this.discountValue = discountValue;
        this.discountPercent = discountPercent;
    }

}