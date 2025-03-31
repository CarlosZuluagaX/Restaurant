package com.restaurant.domain.service;

public class DiscountService {
    private static final double MAX_DISCOUNT_PERCENTAGE = 10.0;

    public double validateCoupon(String couponCode) {
        if (couponCode == null || couponCode.isBlank()) {
            return 0.0;
        }

        try {
            // Formato esperado: "DESC10" para 10% de descuento
            if (couponCode.startsWith("DESC")) {
                String percentageStr = couponCode.substring(4);
                double percentage = Double.parseDouble(percentageStr);

                // Validar que el descuento no exceda el máximo permitido
                if (percentage > 0 && percentage <= MAX_DISCOUNT_PERCENTAGE) {
                    return percentage;
                }
            }
            return 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}