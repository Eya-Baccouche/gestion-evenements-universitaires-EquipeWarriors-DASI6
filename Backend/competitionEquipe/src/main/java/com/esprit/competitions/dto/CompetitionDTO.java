package com.esprit.competitions.dto;

import com.esprit.competitions.entity.StatutCompetition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;

    @NotNull(message = "La taille minimale d'équipe est obligatoire")
    @Min(value = 1, message = "La taille minimale doit être d'au moins 1")
    private Integer tailleEquipeMin;

    @NotNull(message = "La taille maximale d'équipe est obligatoire")
    @Min(value = 1, message = "La taille maximale doit être d'au moins 1")
    private Integer tailleEquipeMax;

    private Integer nbEquipesMax;

    // Coefficients dynamiques (ex: 0.35, 0.40, 0.25)
    @DecimalMin(value = "0.0", message = "Le coefficient ne peut pas être négatif")
    @DecimalMax(value = "1.0", message = "Le coefficient ne peut pas dépasser 1.0")
    private Double coefInnovation;

    @DecimalMin(value = "0.0", message = "Le coefficient ne peut pas être négatif")
    @DecimalMax(value = "1.0", message = "Le coefficient ne peut pas dépasser 1.0")
    private Double coefTechnique;

    @DecimalMin(value = "0.0", message = "Le coefficient ne peut pas être négatif")
    @DecimalMax(value = "1.0", message = "Le coefficient ne peut pas dépasser 1.0")
    private Double coefPitch;

    private StatutCompetition statut;

    @NotNull(message = "L'identifiant de l'événement associé est obligatoire")
    private Long evenementId;
}
