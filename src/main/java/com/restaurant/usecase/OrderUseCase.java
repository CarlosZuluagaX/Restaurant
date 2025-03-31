package com.restaurant.usecase;

import com.restaurant.domain.model.Order;
import com.restaurant.domain.model.OrderStatus;
import com.restaurant.domain.model.Product;
import com.restaurant.domain.repository.OrderRepository;
import com.restaurant.domain.service.DiscountService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderUseCase {
    private final OrderRepository orderRepository;
    private final DiscountService discountService;

    public OrderUseCase(OrderRepository orderRepository, DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.discountService = discountService;
    }

    public Order createOrder(Integer tableNumber) {
        Order order = new Order(tableNumber);
        return orderRepository.save(order);
    }

    public Order addItemToOrder(UUID orderId, Product product, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("No se pueden añadir items a un pedido en estado " + order.getStatus());
        }

        order.addItem(product, quantity);
        order.changeStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }
    public Optional<Order> closeOrder(UUID orderId, String couponCode) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

            if (order.getStatus() != OrderStatus.DELIVERED) {
                throw new IllegalStateException("Solo se pueden cerrar pedidos entregados");
            }

            // Aplicar descuento si el cupón es válido
            if (couponCode != null && !couponCode.isBlank()) {
                double discountPercentage = discountService.validateCoupon(couponCode);
                if (discountPercentage > 0) {
                    double discountAmount = order.calculateSubtotal() * (discountPercentage / 100);
                    order.applyDiscount(discountAmount, discountPercentage);
                }
            }

            order.changeStatus(OrderStatus.CLOSED);
            return Optional.of(orderRepository.save(order));
        } catch (Exception e) {
            System.err.println("Error al cerrar pedido: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean cancelOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CLOSED) {
                        return false;
                    }

                    order.changeStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    return true;
                })
                .orElse(false);
    }

    public List<Order> getActiveOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != OrderStatus.CLOSED &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .toList();
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}