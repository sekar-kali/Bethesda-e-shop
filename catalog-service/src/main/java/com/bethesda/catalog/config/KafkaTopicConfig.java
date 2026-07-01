package com.bethesda.catalog.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declare le topic "catalog.stock-events" avec 3 partitions
 * (permet un traitement parallele par cle de produit) et un
 * facteur de replication de 1, adapte a un cluster Kafka local
 * a un seul broker (KRaft) tel que defini dans docker-compose.yml.
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic stockEventsTopic() {
        return TopicBuilder.name("catalog.stock-events")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000") // 7 jours
                .build();
    }
}
