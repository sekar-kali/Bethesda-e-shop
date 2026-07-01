package com.bethesda.catalog.kafka;

import com.bethesda.catalog.model.ProductCategory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Evenement publie sur le topic "catalog.stock-events" a chaque
 * creation ou mise a jour de stock d'un produit.
 * Consomme par order-service pour la simulation de rupture de stock
 * et par tout futur service d'analytics (Kafka Streams).
 */
public record StockEvent(
        Long productId,
        String productName,
        ProductCategory category,
        BigDecimal price,
        Integer stockQuantity,
        StockEventType type,
        Instant occurredAt
) implements Serializable {

    public enum StockEventType {
        PRODUCT_CREATED,
        STOCK_UPDATED,
        OUT_OF_STOCK
    }

    public static StockEvent of(Long productId, String productName, ProductCategory category,
                                 BigDecimal price, Integer stockQuantity, StockEventType type) {
        return new StockEvent(productId, productName, category, price, stockQuantity, type, Instant.now());
    }
}
