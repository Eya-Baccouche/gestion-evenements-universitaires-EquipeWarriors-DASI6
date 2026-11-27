# Microservice Fidelite et recompenses (Hana Abidi) - port 8086

Spring Boot 3.2 / Java 17 / Maven / MongoDB / RabbitMQ / Keycloak / Swagger.
A placer dans `Backend/fidelite` du depot, branche `feature/fidelite`.
Base sur le fichier de scenarios : points d'engagement, niveaux Bronze/Silver/Gold,
catalogue de recompenses (sweats, goodies, bons de certification des entreprises partenaires).

## Lancer (IntelliJ)
1. MongoDB sur `localhost:27017`. **Si tu as lance l'ancienne version, supprime d'abord la base `fidelite`
   dans Compass** (anciens niveaux ARGENT/OR et action INSCRIPTION incompatibles).
2. `File > Open` : choisir `pom.xml`, attendre Maven (JDK 17, Lombok active).
3. Lancer `FideliteApplication`. Le profil `dev` est actif par defaut (voir `application.yml`).
4. Swagger : http://localhost:8086/swagger-ui.html

### Profil dev (par defaut en local)
- Pas de Keycloak : toutes les requetes sont executees comme `user-123` (roles ADMIN, ORGANISATEUR, PARTICIPANT).
- Donnees de demo : user-123 = 140 pts BRONZE, etudiant-ali = 240 pts SILVER, etudiant-sarra = 510 pts GOLD.
- Postman : en-tetes `X-User-Id` et `X-Roles` pour changer d'utilisateur.
- **Avant de pousser en production** : `SPRING_PROFILES_ACTIVE=prod` (le Dockerfile le fait deja).

## Bareme (modifiable dans application.yml)
| Action | Points | Evenement RabbitMQ | Emetteur |
|---|---|---|---|
| PARTICIPATION a une conference | +50 | `presence.enregistree` | a confirmer avec l'equipe (Evenements / Check-in) |
| AVIS depose | +20 | `avis.envoye` | Feedback et intervenants |
| VICTOIRE_HACKATHON | +300 | `competition.gagnee` | Competitions et equipes |
| ECHANGE | - cout de la recompense | API `POST /rewards/{id}/redeem` | - |

Niveaux : BRONZE 0-199, SILVER 200-499, GOLD 500 et plus (recalcule a chaque mouvement).

## Test dans Swagger (profil dev)
1. `GET /loyalty/me` -> 140 points, BRONZE
2. `GET /loyalty/leaderboard` -> sarra (GOLD), ali (SILVER), user-123 (BRONZE)
3. `GET /loyalty/me/movements` -> historique
4. `GET /rewards` puis `GET /rewards?categorie=SWEAT` (SWEAT, GOODIES, CERTIFICATION)
5. `POST /rewards/{id}/redeem` sur le goodies (80 pts) -> 200, solde 60
6. `POST /rewards/{id}/redeem` sur le sweat (300 pts) -> 409 points insuffisants
7. `POST /rewards` : `{ "nom": "Mug", "categorie": "GOODIES", "coutPoints": 30, "stock": 10 }` -> 201 ; `coutPoints: 0` -> 400
8. `GET /loyalty/accounts/etudiant-ali` -> 200 ; `/loyalty/accounts/inconnu` -> 404

## Test avec RabbitMQ
1. `docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management`
2. `RABBIT_LISTENER_ENABLED=true`, relancer.
3. http://localhost:15672 (guest/guest) > Exchanges > `evenements` > Publish message :

| Routing key | Payload | Effet |
|---|---|---|
| `presence.enregistree` | `{"evenementId":"7","participantId":"user-123"}` | +50 |
| `avis.envoye` | `{"avisId":"1","evenementId":"7","participantId":"user-123","note":5}` | +20 |
| `competition.gagnee` | `{"evenementId":"10","equipeId":"3","participantIds":["user-123","etudiant-ali"]}` | +300 a chaque membre |

Le meme message envoye 2 fois n'ajoute les points qu'une seule fois.

## Endpoints
| Methode | URL | Acces | Codes |
|---|---|---|---|
| GET | /loyalty/me | Participant | 200 |
| GET | /loyalty/me/movements | Participant | 200 |
| GET | /loyalty/leaderboard?limit= | Authentifie | 200 |
| GET | /loyalty/accounts/{participantId} | Admin, Organisateur | 200, 404 |
| GET | /rewards?categorie= | Authentifie | 200 |
| POST | /rewards | Admin | 201, 400, 403 |
| PUT | /rewards/{id} | Admin | 200, 404 |
| POST | /rewards/{id}/redeem | Participant | 200, 404, 409 |

## Base MongoDB `fidelite`
| Collection | Contenu |
|---|---|
| `comptes` | `_id` = participantId, points, niveau |
| `mouvements` | participantId, evenementId, action, points, date |
| `recompenses` | nom, categorie, partenaire, coutPoints, stock (3 recompenses au demarrage) |

## Regles
- Anti-doublon : l'identifiant d'un mouvement PARTICIPATION/AVIS/VICTOIRE_HACKATHON est `participantId_evenementId_ACTION`.
- Echange : stock decremente puis points debites de facon atomique ; 409 si stock epuise ou points insuffisants.
- Aucun appel synchrone vers les autres services, aucun evenement publie.
- `participantId` = id Keycloak (`sub` du jeton). Pas de mot de passe dans le depot.
