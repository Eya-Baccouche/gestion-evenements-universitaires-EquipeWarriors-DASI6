# 🚪 API Gateway (Point d'Entrée Unique)

- **Projet :** Gestion des Événements Universitaires (ESPRIT - 4DAS6)
- **Équipe :** Warriors
- **Technologies :** Spring Boot 3.3.4, Spring Cloud Gateway 2023.0.3, Spring Cloud LoadBalancer, Java 17/21
- **Rôle :** Passerelle d'API centralisée pour router toutes les requêtes front-end vers les microservices appropriés avec équilibrage de charge dynamique.

---

## 📌 Fonctionnalités Clés

1. **Routage Dynamique & Load Balancing :** Résolution dynamique des adresses des microservices via Eureka (`lb://<nom-du-service>`).
2. **Support CORS Global :** Pré-configuré pour accepter les requêtes du Front-end React (Vite / CRA).
3. **Point d'Entrée Unique :** Les clients (Front-end, Mobile, Postman) appellent uniquement la Gateway, sans avoir à connaître l'adresse ou le port individuel de chaque microservice.

---

## 🗺️ Table de Routage Pré-Configurée

| Préfixe de la Route | Microservice Cible | Responsable |
| :--- | :--- | :--- |
| `/api/competitions/**` | `lb://competitions-service` | Eya Baccouche |
| `/api/evenements/**` | `lb://evenements-service` | Eya Ben Saber |
| `/api/sponsors/**` | `lb://sponsors-service` | Rayen Amri |
| `/api/feedback/**` | `lb://feedback-service` | Youssef Saibi |
| `/api/fidelite/**` | `lb://fidelite-service` | Hana Abidi |
| `/api/planning/**` | `lb://planning-service` | Amal Barhoumi |
| `/api/logistique/**` | `lb://logistique-service` | Équipe (Commun) |

---

## 🚀 Comment lancer l'API Gateway ?

### Prérequis :
Lancer d'abord **Eureka Server** pour que la Gateway puisse s'y connecter et découvrir les microservices.

### Depuis IntelliJ IDEA :
1. Importer le projet comme projet Maven (`pom.xml` > **Maven** > **Reload project**).
2. Lancer la classe principale `ApiGatewayApplication.java`.

### Depuis le terminal :
```bash
mvn clean spring-boot:run
```
