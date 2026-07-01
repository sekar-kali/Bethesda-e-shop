# Bethesda E-Shop

Plateforme e-commerce personnelle proposant une sélection de produits indiens
(épices, thés, textiles, artisanat, épicerie), développée pour mettre en
pratique une **architecture orientée événements avec Apache Kafka**.

Projet réalisé par **Kalivaradhan Sekar**.

## Architecture

```
                     ┌────────────────────┐
                     │   Frontend Angular  │
                     │  (catalogue, panier,│
                     │  suivi temps réel)  │
                     └─────────┬──────────┘
                     REST      │      WebSocket (STOMP)
              ┌─────────────────┴─────────────────┐
              ▼                                     ▼
    ┌───────────────────┐                 ┌───────────────────┐
    │  catalog-service   │  Kafka topic    │   order-service    │
    │  (Spring Boot)     │  catalog.       │   (Spring Boot)    │
    │  produits + stock  ├───stock-events─▶│  commandes +       │
    │                     │                 │  vérif. stock async│
    └─────────┬───────────┘                 └─────────┬─────────┘
              │                                        │
              ▼                                        ▼
        PostgreSQL                              PostgreSQL
     (bethesda_catalog)                      (bethesda_orders)

              Apache Kafka (mode KRaft, sans Zookeeper)
```

- **catalog-service** expose l'API REST des produits et **publie** un
  événement Kafka (`catalog.stock-events`) à chaque création de produit ou
  mise à jour de stock.
- **order-service** **consomme** ces événements pour maintenir une vue locale
  du stock (`StockCache`), ce qui lui permet de valider une commande sans
  appel synchrone au catalogue. Il publie à son tour ses propres événements
  (`order.events`) et notifie le frontend en temps réel via WebSocket
  (STOMP/SockJS) à chaque changement de statut de commande.
- **Kafka** tourne en **mode KRaft** (sans Zookeeper), conformément aux
  dernières recommandations Apache Kafka (standard depuis Kafka 3.x / 4.0).

## Stack technique

| Composant | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Kafka, Spring Data JPA, Spring WebSocket |
| Message broker | Apache Kafka (KRaft), 3 partitions par topic |
| Frontend | Angular 18 (standalone components, signals), @stomp/stompjs |
| Base de données | PostgreSQL (une base par service) |
| Conteneurisation | Docker, Docker Compose |
| Tests | JUnit 5, Spring Kafka Test, Testcontainers (Kafka) |
| Observabilité | Spring Actuator, Kafka UI |

### Pourquoi ces choix

- **KRaft plutôt que Zookeeper** : c'est la configuration standard des
  nouvelles versions d'Apache Kafka, plus simple à opérer (un seul processus
  à gérer).
- **Clé de partitionnement = id produit / id commande** : garantit l'ordre
  des événements pour une même entité au sein d'une partition.
- **Producteur idempotent (`acks=all`, `enable.idempotence=true`)** : évite
  la perte ou la duplication d'événements de stock.
- **Consommateur avec accusé de réception manuel** (`MANUAL_IMMEDIATE`) :
  l'offset n'est validé qu'après traitement effectif du message.
- **Vue matérialisée locale (`StockCache`)** côté `order-service` : évite le
  couplage synchrone entre microservices, principe central d'une
  architecture événementielle.
- **JSON aujourd'hui, Avro + Schema Registry en évolution naturelle** : le
  projet utilise `spring-kafka` avec sérialisation JSON pour rester simple à
  lancer en local sans dépendance supplémentaire ; une image `docker-compose`
  Confluent Schema Registry + sérialiseurs Avro serait l'étape suivante pour
  garantir la compatibilité de schéma en production (mentionné en commentaire
  dans le code, voir `KafkaProducerConfig`).

## Lancer le projet en local

### Avec Docker Compose (recommandé)

```bash
git clone https://github.com/sekar-kali/Bethesda-e-shop.git
cd Bethesda-e-shop
docker compose up --build
```

Services disponibles :

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| catalog-service | http://localhost:8081/api/products |
| order-service | http://localhost:8082/api/orders |
| Kafka UI | http://localhost:8090 |

### En développement (sans Docker pour le backend)

```bash
# Démarre uniquement Kafka + Postgres
docker compose up kafka postgres kafka-ui

# Terminal 1 - catalog-service
cd catalog-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2 - order-service
cd order-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3 - frontend
cd frontend
npm install
npm start
```

Le profil `local` utilise une base H2 en mémoire pour éviter d'avoir à
configurer Postgres pendant le développement.

## Structure du repository

```
bethesda-e-shop/
├── catalog-service/     # Spring Boot - API produits + producteur Kafka
├── order-service/       # Spring Boot - API commandes + consommateur Kafka + WebSocket
├── frontend/             # Angular - catalogue, panier, suivi de commande temps réel
├── infra/postgres-init/  # Script de création des bases par service
├── docker-compose.yml    # Orchestration complète (Kafka KRaft, Postgres, services, front)
└── README.md
```

## Roadmap / améliorations possibles

- [ ] Avro + Confluent Schema Registry pour la sérialisation des événements
- [ ] Kafka Streams pour l'agrégation temps réel des ventes par catégorie
- [ ] Authentification (Spring Security / OAuth2)
- [ ] Paiement réel (Stripe) à la place de la simulation `/pay`
- [ ] Déploiement Kubernetes (Helm chart)

## Licence

Projet personnel à but d'apprentissage.
