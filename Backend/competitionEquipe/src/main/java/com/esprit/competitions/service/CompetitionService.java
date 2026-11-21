package com.esprit.competitions.service;

import com.esprit.competitions.dto.CompetitionDTO;
import com.esprit.competitions.entity.*;
import com.esprit.competitions.repository.CompetitionRepository;
import com.esprit.competitions.repository.EquipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final EquipeRepository equipeRepository;

    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    public Competition getCompetitionById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compétition non trouvée avec l'ID : " + id));
    }

    @Transactional
    public Competition createCompetition(CompetitionDTO dto) {
        log.info("Création de la compétition '{}' pour l'événement ID: {}", dto.getTitre(), dto.getEvenementId());

        Competition competition = Competition.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .tailleEquipeMin(dto.getTailleEquipeMin())
                .tailleEquipeMax(dto.getTailleEquipeMax())
                .nbEquipesMax(dto.getNbEquipesMax())
                .coefInnovation(dto.getCoefInnovation() != null ? dto.getCoefInnovation() : 0.35)
                .coefTechnique(dto.getCoefTechnique() != null ? dto.getCoefTechnique() : 0.40)
                .coefPitch(dto.getCoefPitch() != null ? dto.getCoefPitch() : 0.25)
                .statut(dto.getStatut() != null ? dto.getStatut() : StatutCompetition.INSCRIPTION_OUVERTE)
                .evenementId(dto.getEvenementId())
                .build();

        return competitionRepository.save(competition);
    }

    @Transactional
    public Competition changerStatut(Long id, StatutCompetition nouveauStatut) {
        Competition competition = getCompetitionById(id);
        competition.setStatut(nouveauStatut);
        return competitionRepository.save(competition);
    }

    @Transactional
    public Competition cloturerCompetition(Long id) {
        Competition competition = getCompetitionById(id);
        competition.setStatut(StatutCompetition.TERMINEE);
        competitionRepository.save(competition);

        // Récupérer le classement des équipes
        List<Equipe> equipesClassees = equipeRepository.findByCompetitionIdOrderByScoreFinalDesc(id);

        Long equipeGagnanteId = null;
        String nomEquipeGagnante = null;

        if (!equipesClassees.isEmpty()) {
            Equipe gagnante = equipesClassees.get(0);
            equipeGagnanteId = gagnante.getId();
            nomEquipeGagnante = gagnante.getNom();
        }

        log.info("Compétition '{}' (ID: {}) clôturée avec succès. Vainqueur: '{}' (ID: {})",
                competition.getTitre(), competition.getId(), nomEquipeGagnante, equipeGagnanteId);

        return competition;
    }
}
