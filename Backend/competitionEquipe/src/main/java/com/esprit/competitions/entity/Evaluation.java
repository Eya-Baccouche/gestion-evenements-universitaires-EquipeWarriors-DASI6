package com.esprit.competitions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"projet_id", "jury_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jury_id", nullable = false)
    private Long juryId; // ID de l'intervenant / jury

    @Column(nullable = false)
    private Double noteInnovation; // Sur 20

    @Column(nullable = false)
    private Double noteTechnique; // Sur 20

    @Column(nullable = false)
    private Double notePitch; // Sur 20

    @Column(nullable = false)
    private Double noteGlobale; // Moyenne calculée

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateEvaluation = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    @JsonIgnoreProperties("evaluations")
    private Projet projet;

    /**
     * Calcul dynamique de la note globale selon les coefficients de la compétition
     */
    public void calculerNoteGlobale(Double coefInno, Double coefTech, Double coefPitch) {
        double inno = (coefInno != null && coefInno > 0) ? coefInno : 0.35;
        double tech = (coefTech != null && coefTech > 0) ? coefTech : 0.40;
        double pitch = (coefPitch != null && coefPitch > 0) ? coefPitch : 0.25;

        double sommeCoefficients = inno + tech + pitch;
        if (sommeCoefficients <= 0) {
            sommeCoefficients = 1.0;
        }

        double noteBrute = (this.noteInnovation * inno) + (this.noteTechnique * tech) + (this.notePitch * pitch);
        this.noteGlobale = Math.round((noteBrute / sommeCoefficients) * 100.0) / 100.0;
    }

    @PrePersist
    @PreUpdate
    public void fallbackCalcul() {
        if (this.noteGlobale == null && noteInnovation != null && noteTechnique != null && notePitch != null) {
            calculerNoteGlobale(0.35, 0.40, 0.25);
        }
    }
}
