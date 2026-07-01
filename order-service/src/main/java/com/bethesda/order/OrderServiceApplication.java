package com.bethesda.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree du service commande.
 * Expose l'API REST de creation de commande, publie les evenements
 * "order.events" (commande validee / annulee) et consomme les evenements
 * de stock du catalogue pour bloquer les commandes en rupture.
 */
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
