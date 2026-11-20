# Microservice Fidelite et recompenses (Hana Abidi) - port 8086

Spring Boot 3.2 / Java 17 / Maven / MongoDB / RabbitMQ / Keycloak / Swagger.
A placer dans `Backend/fidelite` du depot, branche `feature/fidelite`.
Le dossier de conception detaille est dans `docs/Microservice_Fidelite_Recompenses.pdf`.

## Lancer (IntelliJ)
1. MongoDB doit tourner sur `localhost:27017` (la base `fidelite` est creee automatiquement).
2. `File > Open` : choisir `pom.xml`, attendre le chargement Maven (JDK 17, Lombok active).
3. `Run > Edit Configurations > Modify options > Environment variables` :
   `SPRING_PROFILES_ACTIVE=dev`
4. Lancer `FideliteApplication` (fleche verte).
5. Swagger : http://localhost:8086/swagger-ui.html

### Profil dev (sans Keycloak, Eureka, RabbitMQ)
- Toutes les requetes sont executees comme `user-123` avec les roles ADMIN, ORGANISATEUR, PARTICIPANT.
- Des participants de demo sont crees (user-123 = 70 pts BRONZE, etudiant-ali = 125 pts ARGENT, etudiant-sarra = 350 pts OR).
- Postman : en-tetes `X-User-Id` et `X-Roles` pour changer d'utilisateur (ex: `X-User-Id: etudiant-ali`, `X-Roles: PARTICIPANT`).
- Sans ce profil, la securite JWT Keycloak est active (401 sans jeton).

## Test dans Swagger (profil dev)
1. `GET /loyalty/me` -> 70 points, BRONZE (compte de user-123)
2. `GET /loyalty/leaderboard` -> classement : sarra (OR), ali (ARGENT), user-123 (BRONZE)
3. `GET /loyalty/me/movements` -> historique des gains
4. `GET /loyalty/accounts/etudiant-ali` -> 200 ; `GET /loyalty/accounts/inconnu` -> 404
5. `GET /rewards` -> catalogue (3 recompenses). Copier un `id`.
6. `POST /rewards/{id}/redeem` avec la recompense "Stylo universitaire" (20 pts) -> 200, solde 50
7. Refaire l'echange avec "T-shirt Warriors" (150 pts) -> 409 points insuffisants
8. `POST /rewards` (Admin) : `{ "nom": "Mug", "coutPoints": 30, "stock": 10 }` -> 201 ; avec `coutPoints: 0` -> 400
9. `PUT /rewards/{id}` pour modifier le stock (mettre `stock: 0` puis echanger -> 409 stock epuise)

## Test avec RabbitMQ (gain de points reel)
1. `docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management`
2. Variable d'environnement en plus : `RABBIT_LISTENER_ENABLED=true`, puis relancer.
3. http://localhost:15672 (guest/guest) > Exchanges > `evenements` > Publish message :

| Routing key | Payload | Effet |
|---|---|---|
| `inscription.confirmee` | `{"inscriptionId":"1","evenementId":"7","participantId":"user-123"}` | +5 |
| `presence.enregistree` | `{"presenceId":"1","badgeId":"1","evenementId":"7","participantId":"user-123"}` | +20 |
| `avis.envoye` | `{"avisId":"1","evenementId":"7","participantId":"user-123","note":5}` | +10 |

Envoyer 2 fois le meme message : les points ne sont ajoutes qu'une seule fois.

## Endpoints
| Methode | URL | Acces | Codes |
|---|---|---|---|
| GET | /loyalty/me | Participant | 200 |
| GET | /loyalty/me/movements | Participant | 200 |
| GET | /loyalty/leaderboard?limit= | Authentifie | 200 |
| GET | /loyalty/accounts/{participantId} | Admin, Organisateur | 200, 404 |
| GET | /rewards | Authentifie | 200 |
| POST | /rewards | Admin | 201, 400, 403 |
| PUT | /rewards/{id} | Admin | 200, 404 |
| POST | /rewards/{id}/redeem | Participant | 200, 404, 409 |

## Base MongoDB `fidelite`
| Collection | Contenu |
|---|---|
| `comptes` | `_id` = participantId, points, niveau |
| `mouvements` | participantId, evenementId, action, points, date |
| `recompenses` | nom, coutPoints, stock (3 recompenses creees au demarrage) |

## Regles
- Bareme (modifiable dans `application.yml`) : INSCRIPTION +5, PRESENCE +20, AVIS +10, ECHANGE = -cout.
- Niveaux : BRONZE 0-99, ARGENT 100-299, OR 300+ (recalcule a chaque mouvement).
- Anti-doublon : l'identifiant d'un mouvement INSCRIPTION/PRESENCE/AVIS est `participantId_evenementId_ACTION`,
  MongoDB refuse donc un deuxieme gain identique.
- Echange : stock decremente puis points debites de facon atomique ; 409 si stock epuise ou points insuffisants.
- Aucun appel synchrone vers les autres services, aucun evenement publie.
- `participantId` = id Keycloak (`sub` du jeton). Pas de mot de passe dans le depot.
