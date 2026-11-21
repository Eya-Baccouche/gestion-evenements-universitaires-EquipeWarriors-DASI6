package com.esprit.competitions.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionTermineeEvent implements Serializable {

    private Long competitionId;
    private String titre;
    private Long evenementId;
    private LocalDateTime dateCloture;

    // Podium
    private Long equipeGagnanteId;
    private String nomEquipeGagnante;
    private List<Long> membresGagnantsIds;

    // Tous les participants ayant soumis un projet (pour certificats et points)
    private List<Long> tousParticipantsIds;
}
