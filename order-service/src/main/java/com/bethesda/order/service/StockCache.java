package com.bethesda.order.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache local (materialized view) des niveaux de stock du catalogue,
 * alimente en continu par le consommateur Kafka StockEventListener.
 * Permet a order-service de verifier la disponibilite sans appel
 * synchrone a catalog-service : c'est le principe cle de la
 * communication evenementielle entre les deux microservices.
 */
@Component
public class StockCache {

    private final Map<Long, Integer> stockByProductId = new ConcurrentHashMap<>();

    public void update(Long productId, Integer quantity) {
        stockByProductId.put(productId, quantity);
    }

    public int getAvailableStock(Long productId) {
        return stockByProductId.getOrDefault(productId, 0);
    }

    public boolean isAvailable(Long productId, int requestedQuantity) {
        return getAvailableStock(productId) >= requestedQuantity;
    }
}
