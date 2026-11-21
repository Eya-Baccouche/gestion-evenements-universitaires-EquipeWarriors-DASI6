package com.universite.evenements.entity;

import com.universite.evenements.enums.StatutEvenement;
import com.universite.evenements.enums.TypeEvenement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evenements")
@Getter
@Setter
@NoArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement type;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    private Long salleId;

    private Long organisateurId;

    @Column(nullable = false)
    private Integer capacite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEvenement statut;

    @Column(nullable = false)
    private Boolean materielRestitue = false;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_id")
    private DetailEvenement detail;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SessionProgramme> programme = new ArrayList<>();

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inscription> inscriptions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (statut == null) {
            statut = StatutEvenement.BROUILLON;
        }
        if (materielRestitue == null) {
            materielRestitue = false;
        }
    }
}
