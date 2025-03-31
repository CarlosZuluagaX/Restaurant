package com.restaurant.domain.model;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Order {
    private final UUID id;
    private final Integer tableNumber;
    private final List<OrderItem> items;
    private OrderStatus status;
    private boolean discountApplied;
    private Double discountAmount;
    private Double discountPercentage;

    //private String discountReason; // Motivo o código del cupón (opcional)

    public Order(Integer tableNumber) {
        this.id = UUID.randomUUID();
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.discountApplied = false;
        this.discountAmount = 0.0;

        //this.discountReason = reason; // Registrar motivo

    }

    // ✅ Mantener solo una versión de addItem()
    public void addItem(Product product, Integer quantity) {
        if (product == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("El producto o la cantidad son inválidos.");
        }
        items.add(new OrderItem(product, quantity));
    }

    // ✅ Mantener solo una versión de calculateTotal()
    public Double calculateTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum() - discountAmount;
    }

    public void applyDiscount(double amount, double percentage) {
        if (amount <= 0 || percentage <= 0) {
            throw new IllegalArgumentException("El descuento debe ser positivo");
        }
        this.discountAmount = amount;
        this.discountPercentage = percentage;
    }

    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }




    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

}