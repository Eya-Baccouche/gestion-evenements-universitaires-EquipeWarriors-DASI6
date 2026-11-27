package com.esprit.competitions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String depotGithub;

    @Column(length = 255)
    private String lienPresentation; // Vidéo démo ou slide

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateSoumission = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id", nullable = false, unique = true)
    @JsonIgnoreProperties("projet")
    private Equipe equipe;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("projet")
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();
}
