# Microservice Compétitions & Équipes

- **Responsable :** Eya Baccouche (4DAS6 - ESPRIT)
- **Équipe :** Warriors
- **Technologies :** Spring Boot 3.3.4, Java 17/21, Spring Data JPA, PostgreSQL, Hibernate, Bean Validation, Lombok, Swagger UI (SpringDoc OpenAPI).
- **Rôle :** Gestion complète des hackathons et compétitions universitaires : création des concours, gestion des équipes et codes d'invitation, Smart Matching pour candidats solos, soumission des projets (GitHub & démo), et notation pondérée des jurys.

---

## Ce que j'ai réalisé dans ce microservice

### 1. Modélisation & Persistance (PostgreSQL)
* **7 Entités JPA relationnelles** couvrant tout le cycle de vie du hackathon :
  * `Competition` : Paramétrage du concours, dates, capacité, tailles d'équipe et coefficients de notation.
  * `Equipe` : Nom unique, code d'invitation (8 caractères), leader, table attribuée, score final et classement.
  * `MembreEquipe` : Rôle technique (`LEADER`, `DEVELOPPEUR_BACKEND`, `FRONTEND`, `UI_UX`...) et adhésion.
  * `CandidatureSolo` : Profil et compétences d'un étudiant seul cherchant une équipe.
  * `Projet` : Dépôt GitHub du code source et lien de la vidéo/présentation du pitch.
  * `Evaluation` : Notes des jurys (Innovation, Technique, Pitch sur 20) et feedback détaillé.
  * `Annonce` : Alertes et communication en temps réel (`INFO`, `URGENT`, `DEADLINE`).
* **4 Énumérations** : `StatutCompetition`, `RoleEquipe`, `StatutMatching`, `TypeAnnonce`.
* Génération automatique des tables sous PostgreSQL (`competitions_db`).

### 2. Sécurité des données & DTOs (Data Transfer Objects)
* Création de **DTOs d'entrée** (`CompetitionDTO`, `EquipeDTO`, `ProjetDTO`, `EvaluationDTO`, `CandidatureSoloDTO`, `AnnonceDTO`).
* Sécurisation des formulaires grâce à la **Bean Validation** (`@NotBlank`, `@NotNull`, bornes des notes entre 0 et 20, tailles d'équipe valides).
* Protection contre la manipulation des données (l'étudiant ne peut pas modifier son propre score lors de la soumission).

### 3. Logique métier & Algorithmes
* **Algorithme de Smart Matching :** Recommande automatiquement aux étudiants sans équipe les équipes ayant des places libres et recherchant leurs compétences techniques.
* **Notation pondérée par les jurys :** Calcul automatique de la note globale selon les coefficients de la compétition :
  $$\text{Note Globale} = \frac{\text{Innovation} \times c_1 + \text{Technique} \times c_2 + \text{Pitch} \times c_3}{c_1 + c_2 + c_3}$$
* **Gestion des équipes :** Génération de codes d'invitation uniques à 8 caractères et respect des contraintes d'unicité (un étudiant par équipe).
* **Clôture & Classement :** Calcul de la moyenne des jurys pour chaque équipe, élection du vainqueur et attribution du podium.

### 4. API REST & Documentation Swagger UI
* **16 Endpoints REST** pour administrer les concours, postuler, rejoindre une équipe, soumettre les projets et saisir les notes.
* Intégration de **Swagger UI** pour tester visuellement et directement chaque route dans le navigateur.

### 5. Modélisation UML
* Diagramme de classes métier (`competitions-classe.png`).
* Diagramme de séquence fonctionnel (`competitions-sequence.png`).

---

## Comment tester l'API avec Swagger UI ?

1. Démarrer l'application Spring Boot (`CompetitionsApplication.java`).
2. Ouvrir l'interface interactive Swagger dans le navigateur :
   **`http://localhost:8082/Competitions&Equipes/swagger-ui.html`**
3. Tester les endpoints directement grâce au bouton **"Try it out"** !
