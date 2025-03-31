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
    private double discountAmount;  // Usar double en lugar de Double para evitar problemas con valores nulos
    private double discountPercentage;

    public Order(Integer tableNumber) {
        this.id = UUID.randomUUID();
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.discountApplied = false;
        this.discountAmount = 0.0;
        this.discountPercentage = 0.0;
    }
    // Método para calcular el subtotal (suma de los precios de todos los productos)
    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal) // Obtén el subtotal de cada item
                .sum();                              // Suma los subtotales
    }

    // Agrega un item al pedido
    public void addItem(Product product, Integer quantity) {
        if (product == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("El producto o la cantidad son inválidos.");
        }
        items.add(new OrderItem(product, quantity));
    }

    // Calcula el total del pedido
    public double calculateTotal() {
        double subtotal = items.stream()
                .mapToDouble(OrderItem::getSubtotal)  // Obtén el subtotal de cada item
                .sum();                               // Suma todos los subtotales

        if (discountApplied) {
            double discount = subtotal * (discountPercentage / 100);  // Calcula porcentaje de descuento
            return subtotal - discount;                              // Aplica descuento
        }

        return subtotal;  // Retorna el subtotal si no hay descuento
    }

    // Aplica un descuento al pedido
    public void applyDiscount(double amount, double percentage) {
        if (amount <= 0 || percentage <= 0) {
            throw new IllegalArgumentException("El descuento debe ser positivo");
        }
        this.discountAmount = amount;
        this.discountPercentage = percentage;
        this.discountApplied = true;
    }

    // Cambia el estado del pedido
    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    // Imprime detalles del pedido (incluyendo el descuento, si existe)
    public String printDetails() {
        StringBuilder details = new StringBuilder();
        details.append("\n=================================\n");
        details.append(String.format("📋 Pedido #%s\n", id));
        details.append(String.format("🪑 Mesa: #%d\n", tableNumber));
        details.append(String.format("📊 Estado: %s\n", status));

        details.append("\n🍽️ Productos:\n");
        items.forEach(item ->
                details.append(String.format("- %2d x %-20s %10.2f\n",
                        item.quantity(),
                        item.product().getName(),
                        item.product().getPrice()))
        );

        double subtotal = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();

        details.append(String.format("\n💵 Subtotal: %.2f\n", subtotal));

        if (discountApplied) {
            double discount = subtotal * (discountPercentage / 100);
            details.append(String.format("🎫 Descuento aplicado: -%.2f\n", discount));
        }

        details.append(String.format("\n💰 Total: %.2f\n", calculateTotal()));
        details.append("=================================\n");

        return details.toString();
    }
}