package com.esprit.competitions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipeDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    private String nom;

    private String codeInvitation;

    @NotNull(message = "Le leader de l'équipe est obligatoire")
    private Long leaderId;

    // Référence vers la table physique dans Planning
    private Long tableId;

    private Double scoreFinal;
    private Integer classement;
    private Long competitionId;
    private Integer nombreMembres;
}
