package com.esprit.competitions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 36)
    private String codeInvitation;

    @Column(nullable = false)
    private Long leaderId; // ID étudiant du créateur/capitaine

    // Référence logique vers la table physique gérée dans le microservice Planning (Amal)
    @Column(name = "table_id")
    private Long tableId;

    @Builder.Default
    private Double scoreFinal = 0.0;

    private Integer classement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    @JsonIgnoreProperties("equipes")
    private Competition competition;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("equipe")
    @Builder.Default
    private List<MembreEquipe> membres = new ArrayList<>();

    @OneToOne(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("equipe")
    private Projet projet;
}
