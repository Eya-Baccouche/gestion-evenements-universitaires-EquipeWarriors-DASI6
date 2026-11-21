package com.esprit.competitions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private Integer tailleEquipeMin;

    @Column(nullable = false)
    private Integer tailleEquipeMax;

    private Integer nbEquipesMax;

    // Coefficients d'évaluation configurables par compétition (dynamiques)
    @Column(nullable = false)
    @Builder.Default
    private Double coefInnovation = 0.35; // Par défaut 35%

    @Column(nullable = false)
    @Builder.Default
    private Double coefTechnique = 0.40; // Par défaut 40%

    @Column(nullable = false)
    @Builder.Default
    private Double coefPitch = 0.25; // Par défaut 25%

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutCompetition statut = StatutCompetition.PLANIFIEE;

    // Référence logique vers le microservice Événements (Pivot)
    @Column(nullable = false)
    private Long evenementId;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("competition")
    @Builder.Default
    private List<Equipe> equipes = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("competition")
    @Builder.Default
    private List<CandidatureSolo> candidaturesSolo = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("competition")
    @Builder.Default
    private List<Annonce> annonces = new ArrayList<>();
}
