package com.bethesda.order.kafka;

import com.bethesda.order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC = "order.events";

    private final KafkaTemplate<String, Object> orderKafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> orderKafkaTemplate) {
        this.orderKafkaTemplate = orderKafkaTemplate;
    }

    public void publish(Order order) {
        OrderEvent event = OrderEvent.of(order.getId(), order.getCustomerEmail(), order.getStatus(), order.getTotalAmount());
        orderKafkaTemplate.send(TOPIC, String.valueOf(order.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Echec de publication de l'evenement pour la commande {}", order.getId(), ex);
                    } else {
                        log.info("Evenement {} publie pour la commande {}", event.status(), order.getId());
                    }
                });
    }
}
