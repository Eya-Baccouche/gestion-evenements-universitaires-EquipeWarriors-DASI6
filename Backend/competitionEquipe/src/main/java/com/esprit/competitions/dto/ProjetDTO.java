package com.esprit.competitions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjetDTO {

    private Long id;

    @NotBlank(message = "Le titre du projet est obligatoire")
    private String titre;

    private String description;
    private String depotGithub;
    private String lienPresentation;
    private LocalDateTime dateSoumission;

    @NotNull(message = "L'ID de l'équipe est obligatoire")
    private Long equipeId;
}
