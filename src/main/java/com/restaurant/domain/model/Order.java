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
    private String discountReason; // Motivo o código del cupón

    public Order(Integer tableNumber) {
        if (tableNumber == null || tableNumber <= 0) {
            throw new IllegalArgumentException("Número de mesa inválido");
        }

        this.id = UUID.randomUUID();
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.discountApplied = false;
        this.discountAmount = 0.0;
        this.discountPercentage = 0.0;
        this.discountReason = null;
    }

    /**
     * Añade un ítem al pedido
     * @param product Producto a añadir
     * @param quantity Cantidad del producto
     * @throws IllegalArgumentException Si el producto o cantidad son inválidos
     */
    public void addItem(Product product, Integer quantity) {
        if (product == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Producto y cantidad deben ser válidos");
        }

        // Verificar si el producto ya existe en el pedido
        items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> {
                            // Actualizar cantidad si el producto ya existe
                            int newQuantity = existingItem.getQuantity() + quantity;
                            items.set(items.indexOf(existingItem),
                                    new OrderItem(product, newQuantity));
                        },
                        () -> {
                            // Añadir nuevo ítem si el producto no existe
                            items.add(new OrderItem(product, quantity));
                        }
                );
    }

    /**
     * Calcula el subtotal sin descuentos
     * @return Subtotal del pedido
     */
    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    /**
     * Calcula el total con descuentos aplicados
     * @return Total a pagar
     */
    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        return subtotal - (discountApplied ? discountAmount : 0.0);
    }

    /**
     * Aplica un descuento al pedido
     * @param amount Monto del descuento
     * @param percentage Porcentaje del descuento
     * @param reason Motivo o código del descuento
     * @throws IllegalArgumentException Si el descuento es inválido
     * @throws IllegalStateException Si ya se aplicó un descuento
     */
    public void applyDiscount(double amount, double percentage, String reason) {
        if (amount <= 0 || percentage <= 0) {
            throw new IllegalArgumentException("El descuento debe ser positivo");
        }
        if (discountApplied) {
            throw new IllegalStateException("Ya se aplicó un descuento a este pedido");
        }

        double subtotal = calculateSubtotal();
        if (amount > subtotal * (percentage/100)) {
            throw new IllegalArgumentException("El monto no coincide con el porcentaje");
        }

        this.discountAmount = amount;
        this.discountPercentage = percentage;
        this.discountReason = reason;
        this.discountApplied = true;
    }

    /**
     * Cambia el estado del pedido
     * @param newStatus Nuevo estado
     * @throws IllegalArgumentException Si el estado es inválido
     */
    public void changeStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Estado no puede ser nulo");
        }

        // Validar transiciones de estado
        if (status == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Pedido cancelado no puede cambiar de estado");
        }
        if (status == OrderStatus.CLOSED && newStatus != OrderStatus.CLOSED) {
            throw new IllegalStateException("Pedido cerrado no puede cambiar de estado");
        }

        this.status = newStatus;
    }

    /**
     * Verifica si el pedido puede ser modificado
     * @return true si el pedido puede ser modificado
     */
    public boolean isModifiable() {
        return status == OrderStatus.CREATED || status == OrderStatus.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return String.format("Pedido #%s - Mesa #%d - Estado: %s - Total: $%.2f",
                id, tableNumber, status, calculateTotal());
    }
}
