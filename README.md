# BusinessService

> Microservice **Spring Boot** de gestion commerciale (B2B) de la plateforme **TuniSales** : clients, catalogue, commandes, factures, livraisons, retours, missions terrain et calcul de bonus vendeurs.

[![Java](https://img.shields.io/badge/Java-11-orange)]() [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.3-brightgreen)]() [![JHipster](https://img.shields.io/badge/JHipster-7.9.3-blue)]() [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14.5-lightgrey)]() [![Build](https://img.shields.io/badge/Build-Maven-red)]()

---

## Sommaire

1. [Objectif du projet](#1-objectif-du-projet)
2. [Stack technique](#2-stack-technique)
3. [Architecture globale](#3-architecture-globale)
4. [Structure des dossiers](#4-structure-des-dossiers)
5. [Modèle de domaine](#5-modèle-de-domaine)
6. [API REST](#6-api-rest)
7. [Couche service & logique métier](#7-couche-service--logique-métier)
8. [Multi-tenant](#8-multi-tenant)
9. [Sécurité](#9-sécurité)
10. [Communication inter-services (Feign)](#10-communication-inter-services-feign)
11. [Préoccupations transverses](#11-préoccupations-transverses)
12. [Configuration](#12-configuration)
13. [Flux de fonctionnement de bout en bout](#13-flux-de-fonctionnement-de-bout-en-bout)
14. [Démarrage rapide](#14-démarrage-rapide)
15. [Tests](#15-tests)
16. [Qualité & CI](#16-qualité--ci)
17. [Déploiement & Docker](#17-déploiement--docker)
18. [Observabilité](#18-observabilité)
19. [État du projet](#19-état-du-projet)
20. [Annexes](#20-annexes)

---

## 1. Objectif du projet

**BusinessService** est le microservice **cœur métier** de la plateforme TuniSales. Il porte toute la **chaîne de vente B2B** : référentiel client, catalogue produits, prise de commande, facturation, livraison, gestion des retours et avoirs, visites/missions terrain, primes vendeurs et réclamations.

Il s'insère dans une architecture microservices typique JHipster :

- une **Gateway** (non incluse ici) qui authentifie l'utilisateur, fait l'agrégation API et propage l'en-tête `X-Tenant-Id` ;
- ce service **BusinessService** (présent dépôt) ;
- un service **inventory-service** (port `8081` côté config, exposé via Eureka) consulté en _Feign_ pour la disponibilité stock ;
- un service **platform-service** (port `8082`) consulté en _Feign_ pour la génération documentaire, les notifications et les scores client/commercial ;
- un **JHipster Registry** (Eureka + Spring Cloud Config) sur `http://localhost:8761` qui assure le service discovery et la configuration centralisée.

Le service est **API-only** (cf. [src/main/resources/static/index.html](src/main/resources/static/index.html) qui n'est qu'une page d'information) ; toute UI est portée par une gateway séparée.

---

## 2. Stack technique

| Catégorie                    | Choix                                         | Version                 |
| ---------------------------- | --------------------------------------------- | ----------------------- |
| Langage                      | Java                                          | 11                      |
| Framework applicatif         | Spring Boot                                   | 2.7.3                   |
| Générateur de code           | JHipster                                      | 7.9.3                   |
| Build                        | Maven (wrapper [mvnw](mvnw))                  | 3.2.5+                  |
| Base de données              | PostgreSQL                                    | 14.5                    |
| Migrations                   | Liquibase                                     | 4.15.0                  |
| ORM                          | Hibernate                                     | 5.6.10.Final            |
| Mapping DTO ↔ Entité         | MapStruct                                     | 1.5.2.Final             |
| Cache distribué              | Hazelcast                                     | (intégrée Spring Cache) |
| Sécurité                     | Spring Security + JWT (JJWT)                  | –                       |
| Service discovery            | Spring Cloud Netflix Eureka                   | –                       |
| Configuration centralisée    | Spring Cloud Config (via JHipster Registry)   | –                       |
| Communication inter-services | Spring Cloud OpenFeign + Resilience4j         | –                       |
| Documentation API            | SpringDoc OpenAPI (Swagger UI)                | –                       |
| Métriques                    | Micrometer / Prometheus                       | –                       |
| Logging                      | SLF4J + Logback ; AOP via AspectJ             | –                       |
| Tests intégration            | JUnit 5 + Testcontainers + MockMvc            | –                       |
| Tests architecture           | ArchUnit                                      | 0.22.0                  |
| Packaging Docker             | Jib (image base Eclipse Temurin 11-jre-focal) | –                       |

Pré-requis machine : **Java 11**, **Maven** (le wrapper [mvnw](mvnw)/[mvnw.cmd](mvnw.cmd) est fourni), **Node ≥ 16.17** (uniquement pour les scripts npm et les hooks de qualité), **Docker** (PostgreSQL + JHipster Registry).

---

## 3. Architecture globale

```
                       ┌──────────────────────────────────────┐
                       │       JHipster Registry              │
                       │  Eureka  +  Spring Cloud Config      │
                       │       http://localhost:8761          │
                       └──────────────┬───────────────────────┘
                                      │ register / fetch config
                                      ▼
   ┌──────────┐    HTTP(S) + JWT    ┌────────────────────────┐    Feign (JWT propagé)
   │ Gateway  │ ─────────────────▶  │   BusinessService      │ ────────────────────────▶  inventory-service
   │  (UI +   │   X-Tenant-Id       │   port 8081            │
   │ AuthN)   │                     │   profil dev/prod      │ ────────────────────────▶  platform-service
   └──────────┘                     └──────────┬─────────────┘   (documents, notifications,
                                               │                  scores client/commercial)
                                               │ JDBC
                                               ▼
                                        ┌────────────┐
                                        │ PostgreSQL │
                                        │   14.5     │
                                        └────────────┘
                                               ▲
                                               │ Liquibase
                                               │
                          Hazelcast (cache distribué, port 5701) — entre instances
```

À l'intérieur du microservice, on suit le **modèle en couches JHipster** :

```
  HTTP   ─▶  [web/rest/]  ─▶  [service/]  ─▶  [repository/]  ─▶  [domain/]  ─▶  PostgreSQL
           Controllers     Services + DTO     JpaRepository    Entités JPA
                           + Mapper
                           + Criteria/QueryService
```

Couches transverses : [config/](src/main/java/com/tunisales/business/config/), [security/](src/main/java/com/tunisales/business/security/), [tenant/](src/main/java/com/tunisales/business/tenant/), [client/](src/main/java/com/tunisales/business/client/) (Feign), [aop/logging/](src/main/java/com/tunisales/business/aop/logging/), [event/](src/main/java/com/tunisales/business/event/).

---

## 4. Structure des dossiers

```
BusinessService/
├── src/
│   ├── main/
│   │   ├── java/com/tunisales/business/
│   │   │   ├── BusinessServiceApp.java         # Point d'entrée Spring Boot
│   │   │   ├── ApplicationWebXml.java          # Support packaging WAR
│   │   │   ├── aop/logging/                    # LoggingAspect (AOP, profil dev)
│   │   │   ├── client/                         # Clients Feign vers autres microservices
│   │   │   ├── config/                         # Configurations Spring (sécurité, cache, async, etc.)
│   │   │   ├── domain/                         # Entités JPA (24 classes) + énumérations
│   │   │   ├── event/                          # Événements applicatifs (OrderValidatedEvent)
│   │   │   ├── management/                     # Métriques personnalisées (SecurityMetersService)
│   │   │   ├── repository/                     # Spring Data JPA repositories
│   │   │   ├── security/                       # Spring Security + JWT
│   │   │   │   └── jwt/
│   │   │   ├── service/                        # Services métier (CRUD + logique custom + calculateurs)
│   │   │   │   ├── dto/                        # DTO MapStruct
│   │   │   │   ├── mapper/                     # Mappers MapStruct
│   │   │   │   └── criteria/                   # Critères de filtrage dynamique
│   │   │   ├── tenant/                         # Multi-tenancy (TenantContext / TenantInterceptor)
│   │   │   └── web/rest/                       # Contrôleurs REST
│   │   └── resources/
│   │       ├── config/
│   │       │   ├── application*.yml            # Configs Spring (dev / prod / tls)
│   │       │   ├── bootstrap*.yml              # Spring Cloud Config (chargé avant application.yml)
│   │       │   └── liquibase/changelog/        # 33 changelogs Liquibase (schéma initial + entités + colonnes)
│   │       ├── i18n/messages.properties        # Internationalisation
│   │       ├── static/                         # Page d'accueil informative (API-only)
│   │       └── templates/                      # Templates Thymeleaf (page d'erreur)
│   ├── test/                                   # Tests unitaires + intégration (Testcontainers)
│   └── main/docker/                            # docker-compose pour BDD, registry, monitoring, etc.
├── pom.xml                                     # Build Maven + profils dev/prod/tls/war
├── package.json                                # Scripts npm (wrappers Maven + Docker + Prettier)
├── checkstyle.xml                              # Règle "no http://"
├── sonar-project.properties                    # Config SonarQube
├── .yo-rc.json                                 # Config JHipster (référentiel de génération)
├── .lintstagedrc.js                            # Prettier sur fichiers stagés
└── .husky/pre-commit                           # Hook Git → lint-staged
```

---

## 5. Modèle de domaine

Toutes les entités vivent dans [src/main/java/com/tunisales/business/domain/](src/main/java/com/tunisales/business/domain/) et portent une colonne `tenant_id` (cf. [§ 8](#8-multi-tenant)). Les champs d'audit (`createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) sont fournis par la classe mère [AbstractAuditingEntity](src/main/java/com/tunisales/business/domain/AbstractAuditingEntity.java).

### 5.1 Contextes métier

#### Clients & contacts

| Entité                                                                                | Rôle                                                                                                                                                           |
| ------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [Client](src/main/java/com/tunisales/business/domain/Client.java)                     | Compte client B2B : `taxId`, `clientType`, `creditLimit`, `creditUsed`, `paymentTermsDays`, `clientStatus`, `clientGrade`, `loyaltyScore`, `performanceScore`. |
| [ClientContact](src/main/java/com/tunisales/business/domain/ClientContact.java)       | Personne de contact rattachée à un client (`role` typé `ContactRole`).                                                                                         |
| [ClientAssignment](src/main/java/com/tunisales/business/domain/ClientAssignment.java) | Lien client ↔ zone géographique / vendeur.                                                                                                                     |
| [Zone](src/main/java/com/tunisales/business/domain/Zone.java)                         | Zone commerciale (code + nom), référencée par `ClientAssignment` et `BonusRule`.                                                                               |

#### Catalogue

| Entité                                                                  | Rôle                                                                  |
| ----------------------------------------------------------------------- | --------------------------------------------------------------------- |
| [Product](src/main/java/com/tunisales/business/domain/Product.java)     | Produit : `sku`, `name`, `brand`, `category`, `price`, `discountPct`. |
| [PriceList](src/main/java/com/tunisales/business/domain/PriceList.java) | Tarif borné dans le temps (`validFrom`, `validTo`).                   |
| [Promotion](src/main/java/com/tunisales/business/domain/Promotion.java) | Remise temporelle sur un produit (`discountPct`, `conditionsJson`).   |

#### Commandes

| Entité                                                                          | Rôle                                                                                                                                                                                                                           |
| ------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| [Order](src/main/java/com/tunisales/business/domain/Order.java)                 | Commande client : `orderNumber`, `status` ([OrderStatus](src/main/java/com/tunisales/business/domain/enumeration/OrderStatus.java)), `subtotal`, `discountAmount`, `taxAmount`, `totalAmount`, `negotiation`, `paymentMethod`. |
| [OrderLine](src/main/java/com/tunisales/business/domain/OrderLine.java)         | Ligne de commande (produit, quantité, prix unitaire, remise). Porte le `vendeurLogin` utilisé par le calcul de bonus.                                                                                                          |
| [OrderLineItem](src/main/java/com/tunisales/business/domain/OrderLineItem.java) | Sous-détail d'une ligne (typiquement état de l'article via `ItemCondition`).                                                                                                                                                   |

#### Facturation & retours

| Entité                                                                              | Rôle                                                                                                                      |
| ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| [Invoice](src/main/java/com/tunisales/business/domain/Invoice.java)                 | Facture générée après validation d'une commande : `invoiceNumber`, `amountHt`, `taxAmount`, `amountTtc`, `invoiceStatus`. |
| [InvoiceLine](src/main/java/com/tunisales/business/domain/InvoiceLine.java)         | Ligne de facture (snapshot prix/remise au moment de la facturation).                                                      |
| [SalesReturn](src/main/java/com/tunisales/business/domain/SalesReturn.java)         | Demande de retour rattachée à une facture.                                                                                |
| [SalesReturnLine](src/main/java/com/tunisales/business/domain/SalesReturnLine.java) | Ligne de retour (référence `invoiceLineId`, quantité, montant remboursé).                                                 |
| [CreditNote](src/main/java/com/tunisales/business/domain/CreditNote.java)           | Avoir émis au client.                                                                                                     |

#### Logistique

| Entité                                                                                  | Rôle                                                                   |
| --------------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| [Delivery](src/main/java/com/tunisales/business/domain/Delivery.java)                   | Bon de livraison : `trackingNumber`, `deliveryDate`, `deliveryStatus`. |
| [VehicleInspection](src/main/java/com/tunisales/business/domain/VehicleInspection.java) | Contrôle / inspection d'un véhicule de flotte (`vehicleState`, notes). |

#### Activité terrain

| Entité                                                              | Rôle                                                                 |
| ------------------------------------------------------------------- | -------------------------------------------------------------------- |
| [Visit](src/main/java/com/tunisales/business/domain/Visit.java)     | Visite chez un client (date, objectif via `VisitObjective`, statut). |
| [Mission](src/main/java/com/tunisales/business/domain/Mission.java) | Tâche/affectation terrain (type via `MissionType`, statut, dates).   |

#### Service après-vente

| Entité                                                                  | Rôle                                            |
| ----------------------------------------------------------------------- | ----------------------------------------------- |
| [Complaint](src/main/java/com/tunisales/business/domain/Complaint.java) | Réclamation client (type, statut, description). |

#### Commissions vendeurs

| Entité                                                                    | Rôle                                                                                                      |
| ------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| [BonusRule](src/main/java/com/tunisales/business/domain/BonusRule.java)   | Règle de prime : `productId` (optionnel), `zoneId` (optionnel), `amount`, fenêtre temporelle, `isActive`. |
| [BonusEntry](src/main/java/com/tunisales/business/domain/BonusEntry.java) | Enregistrement de prime crédité à un vendeur sur une ligne de commande validée.                           |

### 5.2 Énumérations principales

Toutes dans [src/main/java/com/tunisales/business/domain/enumeration/](src/main/java/com/tunisales/business/domain/enumeration/) :

`OrderStatus`, `ClientStatus`, `ClientType`, `ClientGrade`, `ContactRole`, `InvoiceStatus`, `SalesReturnStatus`, `CreditNoteStatus`, `DeliveryStatus`, `VisitStatus`, `VisitObjective`, `MissionStatus`, `MissionType`, `ItemCondition`, `PaymentMethod`, `ComplaintType`, `ComplaintStatus`, `VehicleState`.

Le workflow de [OrderStatus](src/main/java/com/tunisales/business/domain/enumeration/OrderStatus.java) (cf. JavaDoc) :

```
   DRAFT ──submit()──▶ SUBMITTED ──validate()──▶ VALIDATED ──(livraison)──▶ DELIVERED
                            │
                            ├──negotiate()────▶ NEGOTIATING
                            │
                            └──reject()───────▶ REJECTED
```

> Les valeurs legacy `enAttente`, `valide`, `enCours`, `livre`, `rejete` sont conservées pour la rétrocompatibilité des jeux de données existants/faker.

---

## 6. API REST

Tous les contrôleurs sont sous le préfixe `/api` et résident dans [src/main/java/com/tunisales/business/web/rest/](src/main/java/com/tunisales/business/web/rest/). Pagination Spring Data, filtrage dynamique via les `*QueryService`, sérialisation via MapStruct DTO, contrôle d'accès `@PreAuthorize`.

| Contrôleur                                                                                                | Préfixe d'URL              | Description                                               |
| --------------------------------------------------------------------------------------------------------- | -------------------------- | --------------------------------------------------------- |
| [ClientResource](src/main/java/com/tunisales/business/web/rest/ClientResource.java)                       | `/api/clients`             | CRUD client + recherche critères.                         |
| [ClientContactResource](src/main/java/com/tunisales/business/web/rest/ClientContactResource.java)         | `/api/client-contacts`     | Contacts rattachés à un client.                           |
| [ProductResource](src/main/java/com/tunisales/business/web/rest/ProductResource.java)                     | `/api/products`            | CRUD catalogue produit.                                   |
| [PriceListResource](src/main/java/com/tunisales/business/web/rest/PriceListResource.java)                 | `/api/price-lists`         | Tarifs bornés dans le temps.                              |
| [PromotionResource](src/main/java/com/tunisales/business/web/rest/PromotionResource.java)                 | `/api/promotions`          | Campagnes promotionnelles.                                |
| [OrderResource](src/main/java/com/tunisales/business/web/rest/OrderResource.java)                         | `/api/orders`              | Cycle de vie commande, publication `OrderValidatedEvent`. |
| [OrderLineResource](src/main/java/com/tunisales/business/web/rest/OrderLineResource.java)                 | `/api/order-lines`         | Lignes de commande.                                       |
| [OrderLineItemResource](src/main/java/com/tunisales/business/web/rest/OrderLineItemResource.java)         | `/api/order-line-items`    | Sous-détails de ligne.                                    |
| [InvoiceResource](src/main/java/com/tunisales/business/web/rest/InvoiceResource.java)                     | `/api/invoices`            | Facturation et lignes facture.                            |
| [SalesReturnResource](src/main/java/com/tunisales/business/web/rest/SalesReturnResource.java)             | `/api/sales-returns`       | Retours produits.                                         |
| [CreditNoteResource](src/main/java/com/tunisales/business/web/rest/CreditNoteResource.java)               | `/api/credit-notes`        | Avoirs.                                                   |
| [DeliveryResource](src/main/java/com/tunisales/business/web/rest/DeliveryResource.java)                   | `/api/deliveries`          | Livraisons.                                               |
| [VisitResource](src/main/java/com/tunisales/business/web/rest/VisitResource.java)                         | `/api/visits`              | Visites commerciales.                                     |
| [MissionResource](src/main/java/com/tunisales/business/web/rest/MissionResource.java)                     | `/api/missions`            | Missions terrain.                                         |
| [ZoneResource](src/main/java/com/tunisales/business/web/rest/ZoneResource.java)                           | `/api/zones`               | Zones géographiques.                                      |
| [BonusRuleResource](src/main/java/com/tunisales/business/web/rest/BonusRuleResource.java)                 | `/api/bonus-rules`         | Règles de prime vendeur.                                  |
| [BonusEntryResource](src/main/java/com/tunisales/business/web/rest/BonusEntryResource.java)               | `/api/bonus-entries`       | Lecture des primes calculées.                             |
| [CommercialScoreResource](src/main/java/com/tunisales/business/web/rest/CommercialScoreResource.java)     | `/api/commercial-scores`   | Performance commerciale (sous-étape 2.12).                |
| [ComplaintResource](src/main/java/com/tunisales/business/web/rest/ComplaintResource.java)                 | `/api/complaints`          | Réclamations clients.                                     |
| [VehicleInspectionResource](src/main/java/com/tunisales/business/web/rest/VehicleInspectionResource.java) | `/api/vehicle-inspections` | Inspections de véhicules.                                 |

**Documentation interactive** : démarrer en profil `api-docs` puis ouvrir `http://localhost:8081/v3/api-docs` (JSON OpenAPI) ou l'UI Swagger embarquée.

---

## 7. Couche service & logique métier

[src/main/java/com/tunisales/business/service/](src/main/java/com/tunisales/business/service/) contient deux familles de classes :

### 7.1 Services CRUD générés

Un `*Service` + un `*QueryService` (filtrage dynamique par critères) par entité :

`ClientService` / `ClientQueryService`, `ProductService` / `ProductQueryService`, `OrderService` / `OrderQueryService`, `OrderLineService`, `OrderLineItemService`, `InvoiceService` / `InvoiceQueryService`, `DeliveryService` / `DeliveryQueryService`, `MissionService` / `MissionQueryService`, `VisitService` / `VisitQueryService`, `PriceListService` / `PriceListQueryService`, `ClientContactService`, `ZoneService`, `ClientAssignmentService`, `PromotionService`, `BonusRuleService`, `SalesReturnService`, `CreditNoteService`, `ComplaintService`, `VehicleInspectionService`.

Les critères sont déclarés dans [service/criteria/](src/main/java/com/tunisales/business/service/criteria/) (`*Criteria.java`) et exploités via les `*QueryService` pour générer des `Predicate` JPA.

DTO et mappers reposent sur **MapStruct** : voir [service/dto/](src/main/java/com/tunisales/business/service/dto/) et [service/mapper/](src/main/java/com/tunisales/business/service/mapper/).

### 7.2 Logique métier custom

| Classe                                                                                                   | Rôle                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| -------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [BonusCalculator](src/main/java/com/tunisales/business/service/BonusCalculator.java)                     | Sous-étape 2.9. Écoute [OrderValidatedEvent](src/main/java/com/tunisales/business/event/OrderValidatedEvent.java) via `@TransactionalEventListener(AFTER_COMMIT)`. Pour chaque ligne de la commande validée, sélectionne la `BonusRule` active **la plus spécifique** (priorité : produit+zone > produit seul > zone seule > globale) et persiste un `BonusEntry` crédité au vendeur (`vendeurLogin` de la ligne). Expose aussi `computeForVendeur(login, YearMonth)` pour totaliser les primes d'un mois. |
| [PaymentEligibilityService](src/main/java/com/tunisales/business/service/PaymentEligibilityService.java) | Vérifie l'éligibilité paiement client (limite de crédit, encours, mode de paiement) avant validation de commande.                                                                                                                                                                                                                                                                                                                                                                                          |
| [DiscountRangeValidator](src/main/java/com/tunisales/business/service/DiscountRangeValidator.java)       | Garde-fou sur les remises (`discountPct`) appliquées aux produits et aux lignes de commande.                                                                                                                                                                                                                                                                                                                                                                                                               |
| [SequenceService](src/main/java/com/tunisales/business/service/SequenceService.java)                     | Génération des numéros métier (commande, facture, retour, avoir…) de manière séquentielle et thread-safe.                                                                                                                                                                                                                                                                                                                                                                                                  |
| [ClientLoyaltyCalculator](src/main/java/com/tunisales/business/service/ClientLoyaltyCalculator.java)     | Sous-étape 2.11. Job planifié (`@Scheduled(cron="${tunisales.business.loyalty-cron}")`) — mensuel, 1er à 02h00 par défaut — qui recalcule le `loyaltyScore` / `clientGrade` de chaque client.                                                                                                                                                                                                                                                                                                              |
| [CommercialScoreCalculator](src/main/java/com/tunisales/business/service/CommercialScoreCalculator.java) | Sous-étape 2.12. Job planifié mensuel (1er à 03h00 par défaut) qui calcule le score de performance commerciale en s'appuyant sur la propriété `tunisales.business.commercial-monthly-target=100000`.                                                                                                                                                                                                                                                                                                       |

Les **événements applicatifs** vivent dans [src/main/java/com/tunisales/business/event/](src/main/java/com/tunisales/business/event/). Pour l'instant, un seul : [OrderValidatedEvent](src/main/java/com/tunisales/business/event/OrderValidatedEvent.java), publié par `OrderService.validate(...)` après que la commande est passée à `VALIDATED` et que la facture associée a été émise.

---

## 8. Multi-tenant

Stratégie : **isolation par en-tête HTTP `X-Tenant-Id` (UUID)**, propagé depuis la Gateway.

| Classe                                                                                  | Rôle                                                                                                                                                                                                                                                                                                                                   |
| --------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [TenantInterceptor](src/main/java/com/tunisales/business/tenant/TenantInterceptor.java) | `HandlerInterceptor` Spring MVC. Sur chaque requête, lit `X-Tenant-Id`, parse en `UUID`, peuple [TenantContext](src/main/java/com/tunisales/business/tenant/TenantContext.java). Si l'en-tête est absent/invalide, ne lève pas — log un warning. Vide le contexte dans `afterCompletion` pour éviter toute fuite entre threads pooled. |
| [TenantContext](src/main/java/com/tunisales/business/tenant/TenantContext.java)         | `ThreadLocal<UUID>` qui transporte l'identifiant tenant pour la durée d'une requête.                                                                                                                                                                                                                                                   |
| [TenantUtils](src/main/java/com/tunisales/business/tenant/TenantUtils.java)             | Pont entre la représentation UUID (gateway) et la colonne `tenant_id BIGINT` des entités héritées. `currentTenantId()` retourne `Math.abs(uuid.getMostSignificantBits())`. En l'absence de tenant (jobs planifiés, tests), fallback sur `DEFAULT_TENANT_ID = 1L`.                                                                      |

L'enregistrement de l'interceptor se fait dans `config/WebConfigurer` (méthode `addInterceptors`). Les services qui créent ou requêtent des entités appellent systématiquement `TenantUtils.currentTenantId()` pour fixer/filtrer la colonne `tenant_id`.

> Multi-tenant a été introduit dans le commit `6994ed6` (« feat(0.2): add multi-tenant support via TenantContext and TenantInterceptor »).

---

## 9. Sécurité

Authentification **stateless JWT** (RFC 7519, lib JJWT). Le token est émis par la Gateway et propagé sur chaque requête entrante via l'en-tête `Authorization: Bearer <jwt>`.

| Composant                                                                                                   | Rôle                                                                                                                                                                                                                                                                 |
| ----------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [SecurityConfiguration](src/main/java/com/tunisales/business/config/SecurityConfiguration.java)             | Configure la chaîne `HttpSecurity` : CSRF off, sessions stateless, règles d'autorisation par route (`permitAll` sur `/management/health`, `/v3/api-docs/**` ; `hasAuthority(ROLE_ADMIN)` sur `/api/admin/**` et `/management/**` ; `authenticated()` sur `/api/**`). |
| [TokenProvider](src/main/java/com/tunisales/business/security/jwt/TokenProvider.java)                       | Création + validation des JWT (signature HMAC, clé Base64 dans `jhipster.security.authentication.jwt.base64-secret`).                                                                                                                                                |
| [JWTFilter](src/main/java/com/tunisales/business/security/jwt/JWTFilter.java)                               | Servlet filter qui lit le header, valide via `TokenProvider`, peuple le `SecurityContextHolder`.                                                                                                                                                                     |
| [JWTConfigurer](src/main/java/com/tunisales/business/security/jwt/JWTConfigurer.java)                       | `SecurityConfigurerAdapter` qui branche `JWTFilter` avant `UsernamePasswordAuthenticationFilter`.                                                                                                                                                                    |
| [SecurityUtils](src/main/java/com/tunisales/business/security/SecurityUtils.java)                           | Helpers `getCurrentUserLogin()`, `getCurrentUserJWT()`, `isCurrentUserInRole(...)`.                                                                                                                                                                                  |
| [SpringSecurityAuditorAware](src/main/java/com/tunisales/business/security/SpringSecurityAuditorAware.java) | Fournit le login courant à Spring Data JPA Auditing (alimente `createdBy` / `lastModifiedBy`).                                                                                                                                                                       |
| `AuthoritiesConstants` (dans [security/](src/main/java/com/tunisales/business/security/))                   | Constantes statiques (`ROLE_ADMIN`, `ROLE_USER`, `ANONYMOUS`).                                                                                                                                                                                                       |
| [SecurityMetersService](src/main/java/com/tunisales/business/management/SecurityMetersService.java)         | Compteurs Micrometer sur les erreurs d'authentification (token invalide, expiré, malformé, non supporté).                                                                                                                                                            |

Les appels Feign sortants ré-injectent le JWT de l'utilisateur courant via [UserFeignClientInterceptor](src/main/java/com/tunisales/business/client/UserFeignClientInterceptor.java), pour conserver le contexte de sécurité bout en bout.

---

## 10. Communication inter-services (Feign)

Tous les clients Feign sont dans [src/main/java/com/tunisales/business/client/](src/main/java/com/tunisales/business/client/). Le circuit breaker Resilience4j est activé globalement (`feign.circuitbreaker.enabled: true`).

| Client                                                                                                            | Service cible       | Usage                                                                                                                                                                                                                              |
| ----------------------------------------------------------------------------------------------------------------- | ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [InventoryClient](src/main/java/com/tunisales/business/client/InventoryClient.java)                               | `inventory-service` | Vérification du stock disponible avant validation d'une commande. Lève [InventoryUnavailableException](src/main/java/com/tunisales/business/client/InventoryUnavailableException.java) si la quantité demandée n'est pas servable. |
| [PlatformDocumentClient](src/main/java/com/tunisales/business/client/PlatformDocumentClient.java)                 | `platform-service`  | Génération documentaire (factures PDF, bons de retour, avoirs).                                                                                                                                                                    |
| [PlatformNotificationClient](src/main/java/com/tunisales/business/client/PlatformNotificationClient.java)         | `platform-service`  | Envoi de notifications/alertes (email, push…).                                                                                                                                                                                     |
| [PlatformClientScoreClient](src/main/java/com/tunisales/business/client/PlatformClientScoreClient.java)           | `platform-service`  | Lecture/écriture du score de fidélité côté plateforme.                                                                                                                                                                             |
| [PlatformPerformanceScoreClient](src/main/java/com/tunisales/business/client/PlatformPerformanceScoreClient.java) | `platform-service`  | Lecture/écriture du score de performance commerciale.                                                                                                                                                                              |
| [UserFeignClientInterceptor](src/main/java/com/tunisales/business/client/UserFeignClientInterceptor.java)         | –                   | Intercepteur transverse qui propage l'en-tête `Authorization: Bearer <jwt>` à tous les appels Feign.                                                                                                                               |

Configuration centralisée dans [FeignConfiguration](src/main/java/com/tunisales/business/config/FeignConfiguration.java).

---

## 11. Préoccupations transverses

| Sujet                     | Implémentation                                                                                                                                                                                                                                                                                                                                                                                    |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Cache distribué**       | [CacheConfiguration](src/main/java/com/tunisales/business/config/CacheConfiguration.java) — Hazelcast, TTL `3600s`, 1 backup en dev. Caches par défaut sur entités JHipster + cluster-aware.                                                                                                                                                                                                      |
| **Logging AOP**           | [LoggingAspect](src/main/java/com/tunisales/business/aop/logging/LoggingAspect.java) — _pointcuts_ sur `@Service`, `@Repository`, `@RestController`. **Actif uniquement en profil `dev`** ([LoggingAspectConfiguration](src/main/java/com/tunisales/business/config/LoggingAspectConfiguration.java)).                                                                                            |
| **Configuration logback** | [logback-spring.xml](src/main/resources/logback-spring.xml), avec [CRLFLogConverter](src/main/java/com/tunisales/business/config/CRLFLogConverter.java) pour échapper les CR/LF (protection log injection).                                                                                                                                                                                       |
| **Async & scheduling**    | [AsyncConfiguration](src/main/java/com/tunisales/business/config/AsyncConfiguration.java) — `ThreadPoolTaskExecutor` (taille, queue, prefix configurables). `@EnableScheduling` global.                                                                                                                                                                                                           |
| **Internationalisation**  | [LocaleConfiguration](src/main/java/com/tunisales/business/config/LocaleConfiguration.java) ; fichiers de messages dans [src/main/resources/i18n/](src/main/resources/i18n/messages.properties).                                                                                                                                                                                                  |
| **Date / fuseau**         | [DateTimeFormatConfiguration](src/main/java/com/tunisales/business/config/DateTimeFormatConfiguration.java) — ISO date pour les requêtes/réponses.                                                                                                                                                                                                                                                |
| **Jackson**               | [JacksonConfiguration](src/main/java/com/tunisales/business/config/JacksonConfiguration.java) — modules `Hibernate6` (lazy-loading safe), `Jdk8`, `JavaTime`.                                                                                                                                                                                                                                     |
| **Migrations BDD**        | Liquibase. Master : [master.xml](src/main/resources/config/liquibase/master.xml) ; changelogs dans [src/main/resources/config/liquibase/changelog/](src/main/resources/config/liquibase/changelog/) (initial schema + 1 par entité + ajouts incrémentaux pour colonnes `negotiation`, `payment_method`, `client_grade`, `discount_pct`, etc.). Contextes : `dev,faker` en dev, `prod` sans faker. |
| **Eureka workaround**     | [EurekaWorkaroundConfiguration](src/main/java/com/tunisales/business/config/EurekaWorkaroundConfiguration.java) — contournement d'un bug d'enregistrement connu sur certaines combinaisons Spring Cloud / Eureka.                                                                                                                                                                                 |
| **Source de données**     | [DatabaseConfiguration](src/main/java/com/tunisales/business/config/DatabaseConfiguration.java) — `@EnableJpaRepositories`, `@EnableJpaAuditing`, `@EnableTransactionManagement`.                                                                                                                                                                                                                 |
| **Audit transverse**      | `@CreatedBy`, `@LastModifiedBy` peuplés par [SpringSecurityAuditorAware](src/main/java/com/tunisales/business/security/SpringSecurityAuditorAware.java).                                                                                                                                                                                                                                          |

---

## 12. Configuration

### 12.1 Fichiers

| Fichier                                                                                | Profil / Rôle                                                                                                |
| -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [application.yml](src/main/resources/config/application.yml)                           | Base commune (JPA, sécurité, management, propriétés `tunisales.*`).                                          |
| [application-dev.yml](src/main/resources/config/application-dev.yml)                   | Dev : PostgreSQL `localhost:5432/BusinessService`, port `8081`, logs DEBUG, Liquibase contextes `dev,faker`. |
| [application-prod.yml](src/main/resources/config/application-prod.yml)                 | Prod : logs INFO, graceful shutdown, gzip, Liquibase contexte `prod`.                                        |
| [application-tls.yml](src/main/resources/config/application-tls.yml)                   | TLS optionnel (template keystore commenté).                                                                  |
| [bootstrap.yml](src/main/resources/config/bootstrap.yml)                               | Client Spring Cloud Config — où trouver le serveur de config (Registry).                                     |
| [bootstrap-prod.yml](src/main/resources/config/bootstrap-prod.yml)                     | Variante prod.                                                                                               |
| [src/test/resources/config/application.yml](src/test/resources/config/application.yml) | Override pour les tests.                                                                                     |

### 12.2 Profils Maven

Déclarés dans [pom.xml](pom.xml) :

| Profil         | Activation       | Effet                                              |
| -------------- | ---------------- | -------------------------------------------------- |
| `dev`          | par défaut       | DevTools, driver PostgreSQL dev.                   |
| `prod`         | `-Pprod`         | Optimisations, plugin Jib, exclusion logs verbeux. |
| `tls`          | `-Ptls`          | Active TLS (HTTPS local).                          |
| `api-docs`     | `-Papi-docs`     | Active SpringDoc OpenAPI / Swagger UI.             |
| `no-liquibase` | `-Pno-liquibase` | Désactive Liquibase au démarrage.                  |
| `war`          | `-Pwar`          | Packaging WAR au lieu de JAR.                      |
| `zipkin`       | `-Pzipkin`       | Inclut Spring Cloud Sleuth + reporter Zipkin.      |

### 12.3 Propriétés métier `tunisales.*`

Extraites de [application.yml](src/main/resources/config/application.yml) (l.249-260) :

```yaml
tunisales:
  platform:
    url: http://localhost:8082 # platform-service
  inventory:
    url: http://localhost:8081 # inventory-service
  business:
    loyalty-cron: 0 0 2 1 * * # 1er du mois à 02:00 (ClientLoyaltyCalculator)
    commercial-score-cron: 0 0 3 1 * * # 1er du mois à 03:00 (CommercialScoreCalculator)
    commercial-monthly-target: 100000 # cible mensuelle de CA utilisée dans le score commercial
```

### 12.4 Variables d'environnement clés

| Variable                                             | Utilité                                                                   |
| ---------------------------------------------------- | ------------------------------------------------------------------------- |
| `SPRING_PROFILES_ACTIVE`                             | ex. `prod,api-docs`.                                                      |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`              | `http://admin:admin@jhipster-registry:8761/eureka` dans Docker.           |
| `SPRING_CLOUD_CONFIG_URI`                            | URL du config server (Registry).                                          |
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD`  | Connexion PostgreSQL.                                                     |
| `JHIPSTER_SLEEP`                                     | Délai (s) avant démarrage, le temps que Postgres + Registry soient prêts. |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | Clé HMAC pour signer/valider les JWT (impératif en prod).                 |

---

## 13. Flux de fonctionnement de bout en bout

### Scénario : un vendeur passe une commande, on facture, on livre

```
┌─Vendeur (Gateway)─┐
│                    │ POST /api/orders            (JWT, X-Tenant-Id)
└────────┬───────────┘
         ▼
┌──────────────────────────────────────────────────────────────────────┐
│ OrderResource                                                        │
│  └─ OrderService.create()                                            │
│       ├─ TenantUtils.currentTenantId() ──▶ Order.tenantId           │
│       ├─ DiscountRangeValidator.check()                              │
│       └─ persist Order + OrderLines (status = DRAFT)                 │
└──────────────────────────────────────────────────────────────────────┘
         │
         ▼ PUT /api/orders/{id}/submit
┌──────────────────────────────────────────────────────────────────────┐
│ OrderService.submit()  → status = SUBMITTED                          │
└──────────────────────────────────────────────────────────────────────┘
         │
         ▼ PUT /api/orders/{id}/validate
┌──────────────────────────────────────────────────────────────────────┐
│ OrderService.validate()                                              │
│  ├─ InventoryClient.checkAvailability(...)   ← Feign vers Inventory  │
│  │       (lève InventoryUnavailableException si insuffisant)         │
│  ├─ PaymentEligibilityService.check(client)  (credit limit / encours)│
│  ├─ status = VALIDATED                                               │
│  ├─ InvoiceService.createFromOrder(order)   ← émet Invoice + lignes  │
│  └─ ApplicationEventPublisher.publishEvent(                          │
│           new OrderValidatedEvent(orderId, invoiceId))               │
└──────────────────────────────────────────────────────────────────────┘
         │
         ▼ (AFTER_COMMIT)
┌──────────────────────────────────────────────────────────────────────┐
│ BonusCalculator.onOrderValidated()  @TransactionalEventListener      │
│  └─ pour chaque OrderLine :                                          │
│       BonusRule rule = pickRule(productId, zoneId)                   │
│       (priorité produit+zone > produit > zone > globale)             │
│       persist new BonusEntry(vendeurLogin, amount, computedAt)       │
└──────────────────────────────────────────────────────────────────────┘
         │
         ▼ POST /api/deliveries
┌──────────────────────────────────────────────────────────────────────┐
│ DeliveryService.create()   → Delivery rattaché à Order               │
│   (puis Order.status passe à DELIVERED côté logistique)              │
└──────────────────────────────────────────────────────────────────────┘
         │
         ▼ (optionnel — retour client)
┌──────────────────────────────────────────────────────────────────────┐
│ SalesReturnService.create() → SalesReturn + lignes                   │
│ CreditNoteService.issue()   → CreditNote rattachée à Invoice         │
│ PlatformDocumentClient.generatePdf(...)  ← Feign vers Platform       │
│ PlatformNotificationClient.notify(...)   ← Feign vers Platform       │
└──────────────────────────────────────────────────────────────────────┘
```

Parallèlement, deux jobs planifiés tournent une fois par mois (1er à 02h00 puis 03h00) : `ClientLoyaltyCalculator` met à jour `loyaltyScore` / `clientGrade`, `CommercialScoreCalculator` met à jour `performanceScore` de chaque client.

---

## 14. Démarrage rapide

### 14.1 Pré-requis

- **Java 11** (`java -version`)
- **Node ≥ 16.17** (cf. `engines` dans [package.json](package.json))
- **Docker / Docker Compose**
- Le wrapper Maven [mvnw](mvnw) / [mvnw.cmd](mvnw.cmd) est fourni — pas besoin d'installer Maven.

### 14.2 Mode développement

```bash
# 1. Démarrer PostgreSQL (port 5432, user/db = BusinessService)
npm run docker:db:up        # alias de docker-compose -f src/main/docker/postgresql.yml up -d

# 2. Démarrer le JHipster Registry (Eureka + Config server, port 8761, admin/admin)
npm run docker:jhipster-registry:up

# 3. Lancer le service (profil dev par défaut, port 8081)
./mvnw
# ou
npm run app:start
```

Le service refusera de démarrer s'il ne parvient pas à joindre le Registry sur `http://localhost:8761` (cf. [README JHipster v7.9.3](https://www.jhipster.tech/documentation-archive/v7.9.3)).

Une fois démarré :

- API : `http://localhost:8081/api/...`
- Health : `http://localhost:8081/management/health` → `{"status":"UP"}`
- OpenAPI JSON : `http://localhost:8081/v3/api-docs` (si profil `api-docs` actif)

### 14.3 Debug

```bash
npm run backend:debug   # JDWP sur le port 8000
```

### 14.4 Build production

```bash
./mvnw -Pprod clean verify
# Produit target/businessservice-*.jar
java -jar target/businessservice-*.jar
```

### 14.5 Image Docker

```bash
npm run java:docker            # Jib → image businessservice
# Stack complète (app + Postgres + Registry) :
docker compose -f src/main/docker/app.yml up -d
```

---

## 15. Tests

| Type                   | Outils                                                                                                                              | Localisation                                                                                                                                                                                                                                                                                                                                           |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Unitaires**          | JUnit 5 + Mockito                                                                                                                   | [src/test/java/](src/test/java/) (ex. [SecurityUtilsUnitTest](src/test/java/com/tunisales/business/security/SecurityUtilsUnitTest.java), [TokenProviderTest](src/test/java/com/tunisales/business/security/jwt/TokenProviderTest.java), [SecurityMetersServiceTests](src/test/java/com/tunisales/business/management/SecurityMetersServiceTests.java)) |
| **Intégration**        | Spring Boot Test + MockMvc + Testcontainers (PostgreSQL)                                                                            | `*ResourceIT.java` (ex. `OrderResourceIT`, `ClientResourceIT`, `InvoiceResourceIT`…), [ExceptionTranslatorIT](src/test/java/com/tunisales/business/web/rest/errors/ExceptionTranslatorIT.java)                                                                                                                                                         |
| **Annotation de base** | `@SpringBootTest(classes = { BusinessServiceApp.class, AsyncSyncConfiguration.class })` + `@AutoConfigureMockMvc` + `@WithMockUser` | [IntegrationTest](src/test/java/com/tunisales/business/IntegrationTest.java)                                                                                                                                                                                                                                                                           |
| **Architecture**       | ArchUnit (validation de la séparation en couches : Config → Web → Service → Repository → Domain)                                    | `TechnicalStructureTest`                                                                                                                                                                                                                                                                                                                               |
| **Sécurité tokens**    | Comptes Micrometer sur erreurs JWT                                                                                                  | [TokenProviderSecurityMetersTests](src/test/java/com/tunisales/business/security/jwt/TokenProviderSecurityMetersTests.java)                                                                                                                                                                                                                            |

Commandes :

```bash
./mvnw test       # unitaires
./mvnw verify     # unitaires + intégration (Testcontainers démarre Postgres en arrière-plan)
npm run ci:backend:test   # pipeline complète (info + javadoc + checkstyle + verify -Pprod)
```

---

## 16. Qualité & CI

| Outil            | Fichier                                                        | Rôle                                                                                            |
| ---------------- | -------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- | ---- | --- | ---- | -------------------------------------------------- |
| **Prettier**     | [.prettierrc](.prettierrc), [.prettierignore](.prettierignore) | Formatage `\*.md                                                                                | json | yml | html | java` (largeur 140, tab 4 en Java, single quotes). |
| **lint-staged**  | [.lintstagedrc.js](.lintstagedrc.js)                           | Exécute Prettier uniquement sur les fichiers stagés.                                            |
| **Husky**        | [.husky/pre-commit](.husky/pre-commit)                         | Hook git → `npx --no-install lint-staged`.                                                      |
| **Checkstyle**   | [checkstyle.xml](checkstyle.xml)                               | Interdit `http://` (force `https://`) — whitelist `maven.apache.org`, `w3.org`.                 |
| **SonarQube**    | [sonar-project.properties](sonar-project.properties)           | Couverture Jacoco, rapports surefire/failsafe, exclusions JWT (S4502) et JPA transient (S3437). |
| **Jacoco**       | plugin Maven                                                   | Coverage XML : `target/site/**/jacoco*.xml`.                                                    |
| **Modernizer**   | plugin Maven                                                   | Détecte les API Java obsolètes.                                                                 |
| **EditorConfig** | [.editorconfig](.editorconfig)                                 | Normalise fin de ligne / indentation côté IDE.                                                  |

---

## 17. Déploiement & Docker

Les fichiers `docker-compose` sont dans [src/main/docker/](src/main/docker/) :

| Fichier                                                                            | Service(s)                                                                 | Usage                                                                                                                                                               |
| ---------------------------------------------------------------------------------- | -------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [postgresql.yml](src/main/docker/postgresql.yml)                                   | PostgreSQL 14.5                                                            | Base de données locale (port `127.0.0.1:5432`).                                                                                                                     |
| [app.yml](src/main/docker/app.yml)                                                 | `businessservice-app` + `businessservice-postgresql` + `jhipster-registry` | Pile complète prête à l'emploi (profil `prod,api-docs`, `JHIPSTER_SLEEP=30`).                                                                                       |
| [jhipster-registry.yml](src/main/docker/jhipster-registry.yml)                     | JHipster Registry 7.3.0                                                    | Eureka + Spring Cloud Config (port 8761, admin/admin). Charge la conf depuis [central-server-config/](src/main/docker/central-server-config/).                      |
| [jhipster-control-center.yml](src/main/docker/jhipster-control-center.yml)         | JHipster Control Center                                                    | UI d'administration (port 7419, optionnel).                                                                                                                         |
| [monitoring.yml](src/main/docker/monitoring.yml)                                   | Prometheus + Grafana                                                       | Métriques (Prometheus 9090, Grafana 3000). Dashboard JVM dans [grafana/provisioning/dashboards/JVM.json](src/main/docker/grafana/provisioning/dashboards/JVM.json). |
| [hazelcast-management-center.yml](src/main/docker/hazelcast-management-center.yml) | Hazelcast Management Center                                                | Dashboard cluster Hazelcast (optionnel).                                                                                                                            |
| [zipkin.yml](src/main/docker/zipkin.yml)                                           | Zipkin                                                                     | Distributed tracing (optionnel, à coupler avec `-Pzipkin`).                                                                                                         |
| [sonar.yml](src/main/docker/sonar.yml)                                             | SonarQube                                                                  | Analyse qualité (port 9001, auth désactivée).                                                                                                                       |

L'image Docker est construite par **Jib** (`npm run java:docker`) à partir de `eclipse-temurin:11-jre-focal`, expose les ports `8081` (HTTP) et `5701/udp` (Hazelcast cluster), entrypoint [src/main/docker/jib/entrypoint.sh](src/main/docker/jib/entrypoint.sh).

---

## 18. Observabilité

| Source                                      | Endpoint / Outil                                                                                                                                                                       |
| ------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- |
| Health                                      | `GET /management/health`                                                                                                                                                               |
| Info                                        | `GET /management/info`                                                                                                                                                                 |
| Métriques Prometheus                        | `GET /management/prometheus`                                                                                                                                                           |
| Métriques JHipster                          | `GET /management/jhimetrics`                                                                                                                                                           |
| Loggers (lecture/écriture niveau dynamique) | `GET                                                                                                                                                                                   | POST /management/loggers` |
| Caches Hazelcast                            | `GET /management/caches`                                                                                                                                                               |
| Liquibase changelog                         | `GET /management/liquibase`                                                                                                                                                            |
| Logs applicatifs (profil dev)               | `LoggingAspect` trace entrée/sortie des méthodes `@Service`, `@Repository`, `@RestController`.                                                                                         |
| Compteurs sécurité                          | [SecurityMetersService](src/main/java/com/tunisales/business/management/SecurityMetersService.java) — `security.authentication.invalid_tokens`, `expired`, `malformed`, `unsupported`. |
| Tracing distribué                           | Optionnel via le profil Maven `zipkin` (cf. [§ 12.2](#122-profils-maven)) + [zipkin.yml](src/main/docker/zipkin.yml).                                                                  |

---

## 19. État du projet

Phase active du PFE TuniSales. Les commits récents (cf. `git log`) montrent les chantiers en cours :

- `feat(0.2)` — multi-tenant via [TenantContext](src/main/java/com/tunisales/business/tenant/TenantContext.java) / [TenantInterceptor](src/main/java/com/tunisales/business/tenant/TenantInterceptor.java) ;
- ajout des produits dans les détails de commande ;
- connexion de la création de commande au backend ;
- statuts de facture ;
- contacts dans le détail client.

Sous-étapes métier référencées dans le code (commentaires JavaDoc) :

| Sous-étape | Composant                                                                                                                                                                                                                                                                                                                               |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.9        | [BonusCalculator](src/main/java/com/tunisales/business/service/BonusCalculator.java) + [BonusRule](src/main/java/com/tunisales/business/domain/BonusRule.java) + [BonusEntry](src/main/java/com/tunisales/business/domain/BonusEntry.java) + [OrderValidatedEvent](src/main/java/com/tunisales/business/event/OrderValidatedEvent.java) |
| 2.11       | [ClientLoyaltyCalculator](src/main/java/com/tunisales/business/service/ClientLoyaltyCalculator.java) (cron mensuel)                                                                                                                                                                                                                     |
| 2.12       | [CommercialScoreCalculator](src/main/java/com/tunisales/business/service/CommercialScoreCalculator.java) + [CommercialScoreResource](src/main/java/com/tunisales/business/web/rest/CommercialScoreResource.java)                                                                                                                        |

---

## 20. Annexes

### 20.1 Glossaire métier

| Terme                  | Signification                                                                                       |
| ---------------------- | --------------------------------------------------------------------------------------------------- |
| **Tenant**             | Locataire / organisation cliente de la plateforme. Identifié par un UUID propagé via `X-Tenant-Id`. |
| **DRAFT**              | Brouillon de commande, modifiable.                                                                  |
| **SUBMITTED**          | Commande soumise par le vendeur, en attente de validation.                                          |
| **VALIDATED**          | Commande validée (stock OK + crédit OK) ; déclenche `OrderValidatedEvent` + facturation.            |
| **NEGOTIATING**        | Commande en cours de négociation commerciale.                                                       |
| **REJECTED**           | Commande rejetée.                                                                                   |
| **DELIVERED**          | Commande livrée.                                                                                    |
| **BonusRule**          | Règle paramétrique de prime vendeur, scopable par produit et/ou zone.                               |
| **BonusEntry**         | Prime effectivement enregistrée à un vendeur sur une ligne de commande validée.                     |
| **Zone**               | Zone commerciale (sectorisation géographique).                                                      |
| **ClientGrade**        | Palier de fidélité du client (calculé mensuellement).                                               |
| **Avoir / CreditNote** | Document de crédit émis suite à un retour ou un litige.                                             |

### 20.2 Liens utiles

- Documentation JHipster v7.9.3 : <https://www.jhipster.tech/documentation-archive/v7.9.3>
- Microservices avec JHipster : <https://www.jhipster.tech/microservices-architecture/>
- Service Discovery (JHipster Registry) : <https://www.jhipster.tech/jhipster-registry/>
- Spring Cloud OpenFeign : <https://spring.io/projects/spring-cloud-openfeign>
- Liquibase : <https://www.liquibase.org/>

### 20.3 Point d'entrée

`com.tunisales.business.BusinessServiceApp` — voir [BusinessServiceApp.java](src/main/java/com/tunisales/business/BusinessServiceApp.java).
