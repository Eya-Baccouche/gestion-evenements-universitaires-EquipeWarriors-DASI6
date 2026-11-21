package com.universite.evenements.entity;

import com.universite.evenements.enums.StatutInscription;
import com.universite.evenements.util.QrCodeSecurityUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "inscriptions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_evenement_participant",
            columnNames = {"evenement_id", "participant_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statut;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(unique = true, length = 64)
    private String qrCode;

    @Column(nullable = false)
    private Boolean present = false;

    private LocalDateTime dateCheckIn;

    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutInscription.CONFIRMEE;
        }
        if (present == null) {
            present = false;
        }
        if (qrCode == null && evenement != null && participantId != null) {
            qrCode = QrCodeSecurityUtil.genererCodeHache(evenement.getId(), participantId);
        }
    }
}
