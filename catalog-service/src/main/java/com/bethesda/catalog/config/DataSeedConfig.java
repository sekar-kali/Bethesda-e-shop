package com.bethesda.catalog.config;

import com.bethesda.catalog.model.Product;
import com.bethesda.catalog.model.ProductCategory;
import com.bethesda.catalog.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Alimente le catalogue avec une selection de produits indiens
 * au demarrage, si la base est vide (utile en local / demo).
 */
@Configuration
public class DataSeedConfig {

    @Bean
    CommandLineRunner seedIndianProducts(ProductRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.saveAll(java.util.List.of(
                    new Product("Garam Masala artisanal", "Melange d'epices traditionnel moulu a la meule de pierre",
                            ProductCategory.EPICES, "Kerala, Inde", new BigDecimal("6.90"), 120),
                    new Product("Safran de Cachemire", "Safran premium recolte a la main, categorie I",
                            ProductCategory.EPICES, "Cachemire, Inde", new BigDecimal("24.50"), 35),
                    new Product("Curcuma en poudre bio", "Curcuma bio a forte teneur en curcumine",
                            ProductCategory.EPICES, "Tamil Nadu, Inde", new BigDecimal("4.20"), 200),
                    new Product("The Darjeeling Premier Flush", "The noir aromatique de la premiere recolte de printemps",
                            ProductCategory.THE, "Darjeeling, Inde", new BigDecimal("12.90"), 80),
                    new Product("Chai Masala", "Melange d'epices a chai (cardamome, cannelle, gingembre, clou de girofle)",
                            ProductCategory.THE, "Assam, Inde", new BigDecimal("7.50"), 150),
                    new Product("The Assam Gold", "The noir corse, ideal avec du lait",
                            ProductCategory.THE, "Assam, Inde", new BigDecimal("9.90"), 100),
                    new Product("Chale Pashmina brodee", "Chale en pashmina tisse a la main, broderie sozni",
                            ProductCategory.TEXTILE, "Cachemire, Inde", new BigDecimal("89.00"), 15),
                    new Product("Sari en soie de Kanchipuram", "Sari traditionnel en soie tissee, motifs dores",
                            ProductCategory.TEXTILE, "Tamil Nadu, Inde", new BigDecimal("149.00"), 8),
                    new Product("Foulard block-print", "Foulard en coton imprime au bloc de bois, motifs Rajasthan",
                            ProductCategory.TEXTILE, "Rajasthan, Inde", new BigDecimal("22.00"), 60),
                    new Product("Statuette Ganesh en laiton", "Statuette artisanale en laiton, finition polie",
                            ProductCategory.ARTISANAT, "Uttar Pradesh, Inde", new BigDecimal("34.90"), 25),
                    new Product("Lampe Diya en terre cuite", "Lot de 6 lampes traditionnelles pour Diwali",
                            ProductCategory.ARTISANAT, "Gujarat, Inde", new BigDecimal("14.90"), 90),
                    new Product("Coffret encens Nag Champa", "Encens artisanal, 12 boites x 15 batons",
                            ProductCategory.ARTISANAT, "Karnataka, Inde", new BigDecimal("18.00"), 70),
                    new Product("Lentilles Toor Dal bio", "Lentilles jaunes bio, riches en proteines",
                            ProductCategory.EPICERIE, "Madhya Pradesh, Inde", new BigDecimal("5.50"), 180),
                    new Product("Riz Basmati vieilli 1 an", "Riz basmati long grain, vieillissement traditionnel",
                            ProductCategory.EPICERIE, "Pendjab, Inde", new BigDecimal("8.90"), 140),
                    new Product("Ghee clarifie artisanal", "Beurre clarifie produit selon la methode traditionnelle Bilona",
                            ProductCategory.EPICERIE, "Rajasthan, Inde", new BigDecimal("13.50"), 55)
            ));
        };
    }
}
