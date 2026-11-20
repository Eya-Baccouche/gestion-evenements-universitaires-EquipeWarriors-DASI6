package com.warriors.planning.dto;

import com.warriors.planning.entities.TableSalle;
import com.warriors.planning.entities.enums.StatutSalle;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSalleDTO {

    private Long id;

    @NotBlank(message = "Le numéro de la table est obligatoire")
    @Size(max = 20, message = "Le numéro ne doit pas dépasser 20 caractères")
    private String numero;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    private Integer capacite;

    @NotNull(message = "La salle est obligatoire")
    private Long salleId;

    private String salleNom;

    private StatutSalle statut;

    private String description;

    public static TableSalleDTO fromEntity(TableSalle t) {
        return TableSalleDTO.builder()
                .id(t.getId())
                .numero(t.getNumero())
                .capacite(t.getCapacite())
                .salleId(t.getSalle() != null ? t.getSalle().getId() : null)
                .salleNom(t.getSalle() != null ? t.getSalle().getNom() : null)
                .statut(t.getStatut())
                .description(t.getDescription())
                .build();
    }
}
