# Diagrammes de Séquence — Microservice Événements (Eya Ben Saber)

![Diagramme de Séquence des Interactions](./diagramme_sequence.png)

Ce document présente les diagrammes de séquence détaillant les interactions synchrones et asynchrones du microservice **Événements**.

---

## 1. Publication d'un événement (Synchrone Planning + Asynchrone RabbitMQ)

Ce flux illustre :
1. **L'appel synchrone (REST / OpenFeign)** vers le microservice **Planning** pour réserver la salle. En cas d'indisponibilité ou de conflit, l'événement reste au statut `BROUILLON`.
2. **L'événement asynchrone (RabbitMQ)** `ÉvénementPublié` diffusé aux autres microservices une fois la publication validée.

```mermaid
sequenceDiagram
    autonumber
    actor Org as Organisateur
    participant EV as Microservice Événements (Eya)
    participant BDD as MySQL (evenements_db)
    participant PL as Microservice Planning (Amal)
    participant MQ as RabbitMQ (Exchange evenements)
    participant NT as Microservice Notifications (Hana)
    participant ST as Microservice Statistiques (Eya B.)

    Org->>EV: PUT /events/{id}/publish
    EV->>BDD: SELECT * FROM evenements WHERE id = {id}
    BDD-->>EV: Evenement (statut = BROUILLON)

    Note over EV,PL: APPEL SYNCHRONE (OpenFeign REST)
    EV->>PL: POST /reservations { salleId, date, capacite }

    alt Cas A : Salle disponible (Succès)
        PL-->>EV: 201 Created (Reservation confirmee)
        EV->>BDD: UPDATE evenements SET statut = 'PUBLIE'
        BDD-->>EV: OK
        EV-->>Org: 200 OK (Evenement publie)

        Note over EV,MQ: DIFFUSION ASYNCHRONE (RabbitMQ)
        EV-)MQ: Publie "evenement.publie" { evenementId, titre, date, salleId }
        MQ-)NT: Consomme EvenementPublie (prepare les annonces)
        MQ-)ST: Consomme EvenementPublie (initialise le tableau de bord)

    else Cas B : Conflit de salle ou Planning arrete (Echec)
        PL-->>EV: 409 Conflict / Erreur reseau
        Note over EV: L'evenement reste en statut BROUILLON
        EV-->>Org: 409 Conflict ("Reservation de salle impossible")
    end
```

---

## 2. Inscription d'un participant (Contrôle capacité + Asynchrone InscriptionConfirmée)

Ce flux illustre :
1. La vérification de l'état `PUBLIE` et de la capacité avec verrouillage transactionnel (`PESSIMISTIC_WRITE`).
2. L'émission asynchrone du message `InscriptionConfirmée` vers RabbitMQ pour déclencher en tâche de fond la création du badge QR, la notification et les statistiques.

```mermaid
sequenceDiagram
    autonumber
    actor Part as Participant
    participant EV as Microservice Événements (Eya)
    participant BDD as MySQL (evenements_db)
    participant MQ as RabbitMQ (Exchange evenements)
    participant CK as Microservice Check-in (Rayen)
    participant NT as Microservice Notifications (Hana)
    participant ST as Microservice Statistiques (Eya B.)

    Part->>EV: POST /events/{id}/registrations { participantId }
    
    Note over EV,BDD: Verrou pessimiste (PESSIMISTIC_WRITE)
    EV->>BDD: findByIdWithLock(id)
    BDD-->>EV: Evenement (capacite = N, statut = 'PUBLIE')

    EV->>BDD: countByEvenementIdAndStatut(id, 'CONFIRMEE')
    BDD-->>EV: nombreInscrits

    alt Places disponibles (nombreInscrits < capacite)
        EV->>BDD: INSERT INTO inscriptions (evenement_id, participant_id, statut='CONFIRMEE', date=NOW())
        BDD-->>EV: Inscription generee (id = X)
        EV-->>Part: 201 Created (InscriptionResponse)

        Note over EV,MQ: DIFFUSION ASYNCHRONE (RabbitMQ)
        EV-)MQ: Publie "inscription.confirmee" { inscriptionId, evenementId, participantId, date }
        
        par Traitements paralleles independants
            MQ-)CK: Consomme InscriptionConfirmee -> Genere le Badge avec QR code unique
            MQ-)NT: Consomme InscriptionConfirmee -> Envoie l'email de confirmation
            MQ-)ST: Consomme InscriptionConfirmee -> Met a jour le compteur d'inscrits
        end

    else Capacite maximale atteinte
        EV-->>Part: 409 Conflict ("Capacite maximale atteinte")
    end
```

---

## 3. Annulation d'un événement (Asynchrone ÉvénementAnnulé)

L'organisateur annule l'événement. Le microservice Événements met à jour le statut et prévient tout le monde de façon asynchrone via RabbitMQ.

```mermaid
sequenceDiagram
    autonumber
    actor Org as Organisateur
    participant EV as Microservice Événements (Eya)
    participant BDD as MySQL (evenements_db)
    participant MQ as RabbitMQ (Exchange evenements)
    participant PL as Microservice Planning (Amal)
    participant NT as Microservice Notifications (Hana)
    participant CK as Microservice Check-in (Rayen)
    participant ST as Microservice Statistiques (Eya B.)

    Org->>EV: PUT /events/{id}/cancel
    EV->>BDD: UPDATE evenements SET statut = 'ANNULE' WHERE id = {id}
    BDD-->>EV: OK
    EV-->>Org: 200 OK (Evenement annule)

    Note over EV,MQ: DIFFUSION ASYNCHRONE (RabbitMQ)
    EV-)MQ: Publie "evenement.annule" { evenementId }
    
    par Reactions des microservices abonnes
        MQ-)PL: Consomme EvenementAnnule -> Libere automatiquement le creneau de la salle
        MQ-)NT: Consomme EvenementAnnule -> Alerte tous les inscrits de l'annulation
        MQ-)CK: Consomme EvenementAnnule -> Desactive / Revoque les badges QR
        MQ-)ST: Consomme EvenementAnnule -> Archive les statistiques
    end
```

---

## 4. Clôture d'un événement (Asynchrone ÉvénementTerminé)

Quand un événement arrive à terme, il passe au statut `TERMINE` et émet le message `ÉvénementTerminé`.

```mermaid
sequenceDiagram
    autonumber
    participant EV as Microservice Événements (Eya)
    participant BDD as MySQL (evenements_db)
    participant MQ as RabbitMQ (Exchange evenements)
    participant FB as Microservice Feedback (Youssef)
    participant NT as Microservice Notifications (Hana)
    participant ST as Microservice Statistiques (Eya B.)

    Note over EV: Fin de l'evenement (date depassee)
    EV->>BDD: UPDATE evenements SET statut = 'TERMINE' WHERE id = {id}
    
    Note over EV,MQ: DIFFUSION ASYNCHRONE (RabbitMQ)
    EV-)MQ: Publie "evenement.termine" { evenementId }

    par Reactions
        MQ-)FB: Consomme EvenementTermine -> Ouvre la possibilite de soumettre un avis
        MQ-)NT: Consomme EvenementTermine -> Envoie le formulaire de satisfaction
        MQ-)ST: Consomme EvenementTermine -> Calcule le bilan final (taux de presence)
    end
```

---

## 5. Synthèse des Interactions

| Type | Interaction | Protocole | Rôle & Justification |
|---|---|---|---|
| **Synchrone** | **Événements ➔ Planning** | HTTP / REST (OpenFeign) | **Blocage nécessaire :** Impossible de publier un événement sans certitude que la salle est libre. Si Planning est injoignable, l'événement reste en brouillon. |
| **Asynchrone** | **`evenement.publie`** | RabbitMQ (Topic `evenements`) | Informe Notifications, Feedback, Planning et Statistiques de la publication. |
| **Asynchrone** | **`inscription.confirmee`** | RabbitMQ (Topic `evenements`) | Déclenche en arrière-plan la génération du QR badge (Check-in), l'email (Notifications) et l'incrémentation (Statistiques). |
| **Asynchrone** | **`evenement.annule`** | RabbitMQ (Topic `evenements`) | Permet à Planning de libérer immédiatement la salle et à Notifications d'avertir les participants. |
| **Asynchrone** | **`evenement.termine`** | RabbitMQ (Topic `evenements`) | Active la collecte des notes et avis dans le service Feedback. |

