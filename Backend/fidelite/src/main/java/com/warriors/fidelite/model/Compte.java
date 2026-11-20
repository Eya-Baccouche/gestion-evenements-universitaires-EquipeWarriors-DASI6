package com.warriors.fidelite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Compte de fidelite : un seul par participant, cree au premier mouvement. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comptes")
public class Compte {

    /** Identifiant Keycloak (sub) du participant. Stocke dans _id. */
    @Id
    private String participantId;

    /** Solde de points actuel (jamais negatif). */
    private int points;

    private NiveauFidelite niveau;
}
