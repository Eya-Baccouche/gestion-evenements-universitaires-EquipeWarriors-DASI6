# Documentation — Microservice Planning des Salles

**Projet :** AWD 2026-2027 — Gestion des événements universitaires — Équipe Warriors  
**Microservice :** Planning des Salles  
**Branche Git :** `planning`

---

## 1. Entités JPA

Package : `com.warriors.planning.entities`

### Batiment

| Champ | Type | Contraintes |
|-------|------|-------------|
| id | Long | PK, auto-généré |
| nom | String | NOT NULL, UNIQUE, @NotBlank, max 100 |
| adresse | String | NOT NULL, @NotBlank, max 255 |
| nbEtages | Integer | NOT NULL, @Min(1) |

Relation : `@OneToMany` vers Salle (un bâtiment contient plusieurs salles)

### Salle

| Champ | Type | Contraintes |
|-------|------|-------------|
| id | Long | PK |
| nom | String | NOT NULL, @NotBlank, max 100 |
| typeSalle | TypeSalle (enum) | NOT NULL, @NotNull |
| capacite | Integer | NOT NULL, @Min(1) |
| etage | Integer | NOT NULL, @Min(0) |
| batiment | Batiment | @ManyToOne, NOT NULL |
| equipements | String | max 500 |
| statut | StatutSalle (enum) | NOT NULL, défaut DISPONIBLE |
| description | String | — |

Contrainte DB : `UNIQUE(nom, batiment_id)` — pas deux salles du même nom dans le même bâtiment

### ReservationSalle

| Champ | Type | Contraintes |
|-------|------|-------------|
| id | Long | PK |
| salle | Salle | @ManyToOne, NOT NULL |
| titre | String | NOT NULL, @NotBlank, max 200 |
| description | String | — |
| dateReservation | LocalDate | NOT NULL, @FutureOrPresent |
| heureDebut | LocalTime | NOT NULL |
| heureFin | LocalTime | NOT NULL, > heureDebut |
| statut | StatutReservation (enum) | NOT NULL, défaut EN_ATTENTE |
| demandePar | String | NOT NULL, @NotBlank, max 150 |
| roleDemandeur | RoleDemandeur (enum) | NOT NULL |
| evenementId | Long | Optionnel (lien Feign vers evenements-service) |
| nombrePersonnes | Integer | NOT NULL, @Min(1) |
| motifRefus | String | Renseigné si REFUSEE |
| dateCreation | LocalDateTime | Auto (@PrePersist) |
| dateModification | LocalDateTime | Auto (@PreUpdate) |

### Énumérations

| Enum | Valeurs |
|------|---------|
| TypeSalle | AMPHI, SALLE_COURS, SALLE_TP, LABORATOIRE, SALLE_REUNION |
| StatutSalle | DISPONIBLE, EN_MAINTENANCE, FERME |
| StatutReservation | EN_ATTENTE, CONFIRMEE, ANNULEE, REFUSEE |
| RoleDemandeur | ADMIN, ORGANISATEUR |

---

## 2. Repositories

Package : `com.warriors.planning.repositories`

### BatimentRepository

| Méthode | Description |
|---------|-------------|
| `findAll()` | Tous les bâtiments |
| `findById(id)` | Recherche par ID |
| `existsByNom(nom)` | Vérifie l'unicité du nom |
| `findByNom(nom)` | Recherche par nom |
| `save(entity)` | Création/modification |
| `delete(entity)` | Suppression |

### SalleRepository

| Méthode | Description |
|---------|-------------|
| `findAll()` | Toutes les salles |
| `findById(id)` | Recherche par ID |
| `findByBatimentId(batimentId)` | Salles d'un bâtiment |
| `findByTypeSalleAndStatut(type, statut)` | Filtre type + statut |
| `findByStatut(statut)` | Salles par statut |
| `existsByNomAndBatimentId(nom, batimentId)` | Unicité nom dans bâtiment |
| `findSallesDisponibles(type, capaciteMin, batimentId, date, heureDebut, heureFin)` | **R-O5** : recherche de salles libres sur un créneau |

### ReservationSalleRepository

| Méthode | Description |
|---------|-------------|
| `findAll()` | Toutes les réservations |
| `findById(id)` | Recherche par ID |
| `findByDemandePar(demandePar)` | **R-O4** : réservations d'un demandeur |
| `findByStatut(statut)` | Réservations par statut |
| `findBySalleId(salleId)` | Réservations d'une salle |
| `existsChevauchement(salleId, date, heureDebut, heureFin, excludeId)` | **R-S1** : vérifie un chevauchement de créneau |
| `findPlanningSalle(salleId, dateDebut, dateFin)` | **R-O6** : planning d'une salle sur une période |
| `findReservationsFiltrees(statut, salleId, demandePar, dateDebut, dateFin)` | **R-A5** : réservations filtrées (admin) |

---

## 3. Services et règles métier

Package : `com.warriors.planning.services`

### 3.1 Règles métier simples (tous rôles)

| Code | Règle | Implémentation |
|------|-------|----------------|
| R-S1 | Pas de chevauchement de créneau pour une même salle | `existsChevauchement()` + contrainte DB `EXCLUDE USING gist` |
| R-S2 | heure_fin > heure_debut | Validation Java + CHECK DB |
| R-S3 | date_reservation >= date du jour | `@FutureOrPresent` + CHECK DB |
| R-S4 | nombre_personnes <= capacite de la salle | `ReservationService.create()` et `update()` |
| R-S5 | La salle doit être DISPONIBLE pour être réservable | `ReservationService.create()` et `update()` |
| R-S6 | Contrôle de saisie : champs obligatoires, formats, longueurs | Bean Validation + Reactive Forms |

### 3.2 Règles métier avancées — Admin

| Code | Règle | Service / Méthode |
|------|-------|-------------------|
| R-A1 | CRUD complet sur bâtiments et salles | `BatimentService.create/update/delete`, `SalleService.create/update/delete` |
| R-A2 | Changer le statut d'une salle (DISPONIBLE / EN_MAINTENANCE / FERME) | `SalleService.changerStatut()` |
| R-A3 | Valider (EN_ATTENTE -> CONFIRMEE) ou refuser (EN_ATTENTE -> REFUSEE) une réservation, motif obligatoire | `ReservationService.valider()` / `refuser()` |
| R-A4 | Annuler une réservation CONFIRMEE (-> ANNULEE) | `ReservationService.annuler()` |
| R-A5 | Consulter toutes les réservations avec filtres (statut, salle, demandeur, dates) | `ReservationService.findFiltrees()` |
| R-A6 | Statistiques d'occupation (taux par salle, top 5) | `StatistiqueService.getStatistiques()` |
| R-A7 | Ne pas supprimer une salle avec réservations actives (EN_ATTENTE ou CONFIRMEE) | `SalleService.delete()` |

### 3.3 Règles métier avancées — Organisateur

| Code | Règle | Service / Méthode |
|------|-------|-------------------|
| R-O1 | Créer une demande de réservation (statut EN_ATTENTE par défaut) | `ReservationService.create()` |
| R-O2 | Modifier sa propre demande uniquement si EN_ATTENTE | `ReservationService.update()` |
| R-O3 | Annuler sa réservation (EN_ATTENTE ou CONFIRMEE -> ANNULEE) | `ReservationService.annuler()` |
| R-O4 | Consulter ses propres réservations | `ReservationService.findByDemandeur()` |
| R-O5 | Rechercher une salle disponible par critères (type, capacité, date, créneau, équipement) | `SalleService.findSallesDisponibles()` |
| R-O6 | Visualiser le planning d'une salle sur une période | `ReservationService.findPlanningSalle()` |

### 3.4 Détail des services

**BatimentService :** findAll, findById, create (vérifie unicité nom), update (vérifie unicité nom), delete (bloque si salles rattachées)

**SalleService :** findAll, findByFiltres, findById, findSallesDisponibles (R-O5), create (vérifie unicité nom+bâtiment), update, delete (R-A7 : bloque si réservations actives), changerStatut (R-A2)

**ReservationService :** findAll, findById, findByDemandeur (R-O4), findFiltrees (R-A5), findPlanningSalle (R-O6), create (R-O1 + R-S1 à R-S5), update (R-O2 : seulement si EN_ATTENTE), annuler (R-O3/R-A4), valider (R-A3 + re-vérifie chevauchement), refuser (R-A3 : motif obligatoire)

**StatistiqueService :** getStatistiques (R-A6 : totaux par statut, taux d'occupation par salle, top 5 salles)

---

## 4. API REST

Base URL : `http://localhost:8085/api/planning`

### Batiments — `/batiments` (ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/batiments` | Liste des bâtiments |
| GET | `/batiments/{id}` | Détail |
| POST | `/batiments` | Créer |
| PUT | `/batiments/{id}` | Modifier |
| DELETE | `/batiments/{id}` | Supprimer |

### Salles — `/salles`

| Méthode | Endpoint | Rôle | Règle | Description |
|---------|----------|------|-------|-------------|
| GET | `/salles` | ALL | — | Liste filtrée (type, statut, batimentId) |
| GET | `/salles/{id}` | ALL | — | Détail |
| GET | `/salles/disponibles` | ALL | R-O5 | Salles disponibles (date, créneau) |
| GET | `/salles/{id}/planning` | ALL | R-O6 | Planning d'une salle |
| POST | `/salles` | ADMIN | R-A1 | Créer |
| PUT | `/salles/{id}` | ADMIN | R-A1 | Modifier |
| DELETE | `/salles/{id}` | ADMIN | R-A7 | Supprimer |
| PATCH | `/salles/{id}/statut` | ADMIN | R-A2 | Changer statut |

### Réservations — `/reservations`

| Méthode | Endpoint | Rôle | Règle | Description |
|---------|----------|------|-------|-------------|
| GET | `/reservations` | ADMIN | R-A5 | Toutes |
| GET | `/reservations/filtres` | ADMIN | R-A5 | Filtrées |
| GET | `/reservations/mes-reservations` | ORG | R-O4 | Les miennes |
| GET | `/reservations/{id}` | ALL | — | Détail |
| POST | `/reservations` | ORG/ADMIN | R-O1 | Créer |
| PUT | `/reservations/{id}` | ORG | R-O2 | Modifier |
| PATCH | `/reservations/{id}/annuler` | ORG/ADMIN | R-O3/R-A4 | Annuler |
| PATCH | `/reservations/{id}/valider` | ADMIN | R-A3 | Valider |
| PATCH | `/reservations/{id}/refuser` | ADMIN | R-A3 | Refuser (motif) |

### Statistiques — `/statistiques` (ADMIN)

| Méthode | Endpoint | Règle | Description |
|---------|----------|-------|-------------|
| GET | `/statistiques/occupation` | R-A6 | Statistiques d'occupation |

---

## 5. DTOs

| DTO | Usage |
|-----|-------|
| BatimentDTO | Création/lecture bâtiment |
| SalleDTO | Création/lecture salle |
| ReservationDTO | Lecture réservation |
| ReservationCreateDTO | Création/modification réservation (validation complète) |
| StatutDTO | Changement de statut salle |
| StatistiqueDTO | Statistiques d'occupation |

---

## 6. Gestion des erreurs

| Classe | HTTP | Usage |
|--------|------|-------|
| ResourceNotFoundException | 404 | Ressource introuvable |
| BusinessException | 400 | Règle métier violée |
| GlobalExceptionHandler | — | Centralise toutes les erreurs + détails validation |

---

## 7. Base de données PostgreSQL

**3 tables :** `batiment`, `salle`, `reservation_salle`

**Relations :**
```
batiment (1) ──< salle (N)
salle (1) ──< reservation_salle (N)
```

**Contraintes clés :**
- Unicité nom bâtiment + unicité (nom salle, bâtiment)
- CHECK sur tous les enums
- `heure_fin > heure_debut` et `date >= CURRENT_DATE`
- `ON DELETE RESTRICT` sur les FK
- `EXCLUDE USING gist` anti-chevauchement (R-S1) via extension `btree_gist`

**6 index :**

| Index | Table | Colonnes |
|-------|-------|----------|
| idx_salle_batiment | salle | batiment_id |
| idx_salle_type_statut | salle | type_salle, statut |
| idx_reservation_salle | reservation_salle | salle_id |
| idx_reservation_date | reservation_salle | date_reservation |
| idx_reservation_statut | reservation_salle | statut |
| idx_reservation_demandeur | reservation_salle | demande_par |

**Données de test :** 3 bâtiments, 10 salles, 5 réservations

---

## 8. Frontend Angular 17

### Composants (11)

| Composant | Route | Rôle | Règles |
|-----------|-------|------|--------|
| SalleListComponent | `/salles` | ALL | Liste + suppression (R-A7) |
| SalleDisponiblesComponent | `/salles/disponibles` | ALL | R-O5 : recherche par critères |
| SalleFormComponent | `/salles/nouvelle`, `/:id/edit` | ADMIN | R-A1 : création/modification |
| SalleDetailComponent | `/salles/:id` | ALL | R-O6 + R-A2 |
| BatimentListComponent | `/batiments` | ADMIN | Liste + suppression |
| BatimentFormComponent | `/batiments/nouveau`, `/:id/edit` | ADMIN | R-A1 |
| ReservationListComponent | `/reservations` | ADMIN | R-A5 + R-A3 |
| ReservationMesReservationsComponent | `/reservations/mes-reservations` | ORG | R-O4 + R-O3 |
| ReservationFormComponent | `/reservations/nouvelle`, `/:id/edit` | ORG | R-O1/R-O2 |
| ReservationDetailComponent | `/reservations/:id` | ALL | Actions selon rôle |
| StatistiqueOccupationComponent | `/statistiques` | ADMIN | R-A6 |

### Services frontend (5)

| Service | Endpoints appelés |
|---------|-------------------|
| BatimentService | CRUD `/batiments` |
| SalleService | CRUD `/salles` + `/disponibles` + `/{id}/planning` + `/{id}/statut` |
| ReservationService | CRUD `/reservations` + `/filtres` + `/mes-reservations` + valider/refuser/annuler |
| StatistiqueService | `/statistiques/occupation` |
| AuthService | Simulation de rôle (Admin/Organisateur) — Keycloak en production |

### Contrôle de saisie frontend

- Reactive Forms avec Validators (required, min, max, maxlength)
- Validation heure_fin > heure_debut côté client
- Affichage des erreurs par champ
- Affichage des erreurs API (BusinessException, validation)

---

## 9. Configuration

### application.yml (production)

```yaml
server.port: 8085
spring.datasource.url: jdbc:postgresql://localhost:5432/planning_db
spring.datasource.username: postgres
spring.datasource.password: postgres
spring.jpa.hibernate.ddl-auto: none
spring.sql.init.mode: always
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/
```

### Profils

| Profil | Base de données | Usage |
|--------|-----------------|-------|
| défaut | PostgreSQL | Production / développement |
| dev | H2 mémoire | Démo rapide sans PostgreSQL |
| test | H2 mémoire | Tests unitaires |

---

## 10. Lancement

### Backend
```bash
cd Backend/planning
mvn spring-boot:run                                    # avec PostgreSQL
mvn spring-boot:run -Dspring-boot.run.profiles=dev     # sans PostgreSQL (H2)
```

### Frontend
```bash
cd Frontend
npm install
npm start          # http://localhost:4200
```

### Pré-requis
- Java 17, Maven 3.9+
- PostgreSQL 18 (ou profil dev pour H2)
- Node.js 20+ pour le frontend
