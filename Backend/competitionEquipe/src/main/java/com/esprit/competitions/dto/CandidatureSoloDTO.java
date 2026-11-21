package com.esprit.competitions.dto;

import com.esprit.competitions.entity.RoleEquipe;
import com.esprit.competitions.entity.StatutMatching;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatureSoloDTO {

    private Long id;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotBlank(message = "Veuillez renseigner vos compétences")
    private String competences;

    @NotEmpty(message = "Veuillez sélectionner au moins un rôle souhaité")
    private Set<RoleEquipe> rolesSouhaites;

    // Précision si AUTRE est sélectionné
    private String rolePersonnalise;

    private StatutMatching statut;
    private Long competitionId;
}
