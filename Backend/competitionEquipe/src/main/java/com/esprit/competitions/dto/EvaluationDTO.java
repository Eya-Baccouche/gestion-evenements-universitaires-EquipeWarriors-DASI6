package com.esprit.competitions.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationDTO {

    private Long id;

    @NotNull(message = "L'ID du jury est obligatoire")
    private Long juryId;

    @NotNull(message = "La note d'innovation est obligatoire")
    @Min(value = 0, message = "La note minimale est 0")
    @Max(value = 20, message = "La note maximale est 20")
    private Double noteInnovation;

    @NotNull(message = "La note technique est obligatoire")
    @Min(value = 0, message = "La note minimale est 0")
    @Max(value = 20, message = "La note maximale est 20")
    private Double noteTechnique;

    @NotNull(message = "La note de pitch est obligatoire")
    @Min(value = 0, message = "La note minimale est 0")
    @Max(value = 20, message = "La note maximale est 20")
    private Double notePitch;

    private Double noteGlobale;
    private String feedback;
    private LocalDateTime dateEvaluation;

    @NotNull(message = "L'ID du projet est obligatoire")
    private Long projetId;
}
