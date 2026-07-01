package com.bethesda.order.kafka;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Copie du contrat d'evenement expose par catalog-service sur le topic
 * "catalog.stock-events". Dupliquer volontairement le DTO cote consommateur
 * (plutot qu'un module partage) permet aux deux services d'evoluer et d'etre
 * deployes independamment, au prix d'une contrainte de compatibilite geree
 * ici manuellement (voir README : en production, cette compatibilite serait
 * garantie par Avro + Schema Registry plutot que par convention).
 */
public record StockEvent(
        Long productId,
        String productName,
        String category,
        BigDecimal price,
        Integer stockQuantity,
        String type,
        Instant occurredAt
) implements Serializable {

    public boolean isOutOfStock() {
        return "OUT_OF_STOCK".equals(type) || (stockQuantity != null && stockQuantity == 0);
    }
}
