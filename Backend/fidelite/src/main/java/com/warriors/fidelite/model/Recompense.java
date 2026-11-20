package com.warriors.fidelite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recompenses")
public class Recompense {

    @Id
    private String id;

    private String nom;

    /** Nombre de points necessaires pour obtenir la recompense. */
    private int coutPoints;

    /** Quantite disponible. A 0, l'echange est refuse. */
    private int stock;
}
