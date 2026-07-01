package com.bethesda.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree du service catalogue.
 * Expose l'API REST des produits et publie les evenements de stock sur Kafka
 * (topic "catalog.stock-events") a chaque creation / mise a jour de produit.
 */
@SpringBootApplication
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }
}
