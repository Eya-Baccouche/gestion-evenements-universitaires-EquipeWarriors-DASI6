package com.warriors.fidelite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Historique des gains et des depenses de points.
 *
 * Anti-doublon : pour INSCRIPTION, PRESENCE et AVIS, l'identifiant est deterministe
 * (participantId_evenementId_ACTION), donc MongoDB refuse un 2e gain identique.
 * Pour ECHANGE, l'identifiant est genere automatiquement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mouvements")
public class Mouvement {

    @Id
    private String id;

    @Indexed
    private String participantId;

    /** Evenement a l'origine des points. Vide pour un ECHANGE. */
    private Long evenementId;

    private ActionFidelite action;

    /** Positif pour un gain, negatif pour une depense. */
    private int points;

    private LocalDateTime date;
}
