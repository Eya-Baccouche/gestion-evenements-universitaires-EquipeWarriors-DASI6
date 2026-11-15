package com.esprit.competitions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "membres_equipe", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"equipe_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembreEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID étudiant de Keycloak

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RoleEquipe role = RoleEquipe.DEVELOPPEUR_FULLSTACK;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateAdhesion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id", nullable = false)
    @JsonIgnoreProperties("membres")
    private Equipe equipe;
}
