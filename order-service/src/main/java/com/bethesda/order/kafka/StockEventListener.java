package com.bethesda.order.kafka;

import com.bethesda.order.service.StockCache;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Consomme le topic "catalog.stock-events" publie par catalog-service
 * et met a jour la vue locale du stock (StockCache), utilisee pour
 * valider les commandes sans appel synchrone entre services.
 */
@Component
public class StockEventListener {

    private static final Logger log = LoggerFactory.getLogger(StockEventListener.class);

    private final StockCache stockCache;

    public StockEventListener(StockCache stockCache) {
        this.stockCache = stockCache;
    }

    @KafkaListener(topics = "catalog.stock-events", containerFactory = "kafkaListenerContainerFactory")
    public void onStockEvent(ConsumerRecord<String, StockEvent> record, Acknowledgment ack) {
        StockEvent event = record.value();
        try {
            stockCache.update(event.productId(), event.stockQuantity());
            log.info("Stock mis a jour pour le produit {} : {} unites disponibles ({})",
                    event.productId(), event.stockQuantity(), event.type());
        } finally {
            ack.acknowledge();
        }
    }
}
