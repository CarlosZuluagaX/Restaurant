package com.restaurant.domain.model;


public record OrderItem(Product product, int quantity) {

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}
