package com.universite.evenements.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.universite.evenements.enums.TypeSession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "sessions_programme")
@Getter
@Setter
@NoArgsConstructor
public class SessionProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSession typeSession;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    private String animateurOuEntreprise;

    private String emplacement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    @JsonIgnore
    private Evenement evenement;
}
