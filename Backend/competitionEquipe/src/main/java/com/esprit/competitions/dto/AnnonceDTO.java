package com.esprit.competitions.dto;

import com.esprit.competitions.entity.TypeAnnonce;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceDTO {

    private Long id;

    @NotBlank(message = "Le titre de l'annonce est obligatoire")
    private String titre;

    @NotBlank(message = "Le message de l'annonce est obligatoire")
    private String message;

    private TypeAnnonce type;
    private LocalDateTime datePublication;
    private Long competitionId;
}
