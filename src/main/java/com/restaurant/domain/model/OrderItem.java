package com.restaurant.domain.model;

import lombok.Getter;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @param product  Devuelve el producto asociado Producto asociado al pedido
 * @param quantity Devuelve la cantidad del producto Cantidad del producto
 */

public record OrderItem(Product product, int quantity) {
    public OrderItem {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
    }

    // Calcula el subtotal (precio del producto * cantidad)
    public double calculateSubtotal() {
        return product.getPrice();
    }

    public double getSubtotal() {
        return calculateSubtotal();
    }

    @Override
    public String toString() {
        return String.format("%d x %s -> Subtotal: %s",
                quantity,
                product.getName(),
                NumberFormat.getCurrencyInstance(new Locale("es", "CO")).format(calculateSubtotal()));
    }
}