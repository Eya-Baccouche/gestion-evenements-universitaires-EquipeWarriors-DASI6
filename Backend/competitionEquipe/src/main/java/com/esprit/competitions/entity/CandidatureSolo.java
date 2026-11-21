package com.esprit.competitions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "candidatures_solo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatureSolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String competences; // Ex: "Java, Spring Boot, React, Docker"

    // Multi-sélection des rôles (l'étudiant peut cocher plusieurs rôles)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "candidature_roles", joinColumns = @JoinColumn(name = "candidature_id"))
    @Column(name = "role", length = 30)
    @Builder.Default
    private Set<RoleEquipe> rolesSouhaites = new HashSet<>();

    // Champ texte libre si l'étudiant sélectionne AUTRE
    @Column(length = 100)
    private String rolePersonnalise;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutMatching statut = StatutMatching.EN_ATTENTE;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateCandidature = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    @JsonIgnoreProperties("candidaturesSolo")
    private Competition competition;
}
