package com.esprit.competitions.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvenementDTO {
    private Long id;
    private String titre;
    private String description;
    private String type; // HACKATHON, CONFERENCE, FORUM, COMPETITION
    private String statut; // PLANIFIE, EN_COURS, TERMINE
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer capaciteMax;
}
