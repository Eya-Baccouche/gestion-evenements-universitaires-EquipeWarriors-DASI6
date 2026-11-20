package com.warriors.planning.entities;

import com.warriors.planning.entities.enums.StatutSalle;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entité TableSalle — une table individuelle dans une salle.
 * Permet la réservation au niveau table (en plus de la salle entière).
 */
@Entity
@Table(name = "table_salle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de la table est obligatoire")
    @Size(max = 20, message = "Le numéro ne doit pas dépasser 20 caractères")
    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Column(name = "capacite", nullable = false)
    private Integer capacite;

    @NotNull(message = "La salle est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private StatutSalle statut = StatutSalle.DISPONIBLE;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
