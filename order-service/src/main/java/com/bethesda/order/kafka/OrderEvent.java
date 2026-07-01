package com.bethesda.order.kafka;

import com.bethesda.order.model.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Evenement publie sur le topic "order.events" a chaque changement
 * d'etat d'une commande. Consomme potentiellement par un futur
 * service de notification / analytics.
 */
public record OrderEvent(
        Long orderId,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant occurredAt
) implements Serializable {

    public static OrderEvent of(Long orderId, String customerEmail, OrderStatus status, BigDecimal totalAmount) {
        return new OrderEvent(orderId, customerEmail, status, totalAmount, Instant.now());
    }
}
