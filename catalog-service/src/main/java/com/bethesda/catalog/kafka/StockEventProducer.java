package com.bethesda.catalog.kafka;

import com.bethesda.catalog.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockEventProducer {

    private static final Logger log = LoggerFactory.getLogger(StockEventProducer.class);
    private static final String TOPIC = "catalog.stock-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StockEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publie un evenement de stock. La cle du message est l'id du produit :
     * cela garantit que tous les evenements relatifs a un meme produit
     * arrivent dans l'ordre sur la meme partition.
     */
    public void publish(Product product, StockEvent.StockEventType type) {
        StockEvent event = StockEvent.of(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStockQuantity() == 0 ? StockEvent.StockEventType.OUT_OF_STOCK : type
        );

        kafkaTemplate.send(TOPIC, String.valueOf(product.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Echec de publication de l'evenement de stock pour le produit {}", product.getId(), ex);
                    } else {
                        log.info("Evenement {} publie pour le produit {} (offset={})",
                                event.type(), product.getId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
