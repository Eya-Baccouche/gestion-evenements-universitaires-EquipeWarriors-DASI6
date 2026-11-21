package com.esprit.competitions.service;

import com.esprit.competitions.dto.CandidatureSoloDTO;
import com.esprit.competitions.entity.*;
import com.esprit.competitions.repository.CandidatureSoloRepository;
import com.esprit.competitions.repository.CompetitionRepository;
import com.esprit.competitions.repository.EquipeRepository;
import com.esprit.competitions.repository.MembreEquipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartMatchingService {

    private final CandidatureSoloRepository candidatureRepository;
    private final CompetitionRepository competitionRepository;
    private final EquipeRepository equipeRepository;
    private final MembreEquipeRepository membreEquipeRepository;

    @Transactional
    public CandidatureSolo postulerSolo(CandidatureSoloDTO dto) {
        Competition competition = competitionRepository.findById(dto.getCompetitionId())
                .orElseThrow(() -> new RuntimeException("Compétition introuvable"));

        if (membreEquipeRepository.existsByEquipeCompetitionIdAndUserId(dto.getCompetitionId(), dto.getUserId())) {
            throw new RuntimeException("Vous appartenez déjà à une équipe pour cette compétition");
        }

        if (candidatureRepository.existsByCompetitionIdAndUserId(dto.getCompetitionId(), dto.getUserId())) {
            throw new RuntimeException("Vous avez déjà une candidature solo en cours pour cette compétition");
        }

        CandidatureSolo candidature = CandidatureSolo.builder()
                .userId(dto.getUserId())
                .competences(dto.getCompetences())
                .rolesSouhaites(dto.getRolesSouhaites())
                .rolePersonnalise(dto.getRolePersonnalise())
                .statut(StatutMatching.EN_ATTENTE)
                .competition(competition)
                .build();

        return candidatureRepository.save(candidature);
    }

    /**
     * Algorithme Smart Matching Multi-Rôles :
     * Analyse les équipes incomplètes et propose celles où au moins un des rôles
     * souhaités par l'étudiant est actuellement manquant.
     */
    public List<Equipe> trouverEquipesCompatibles(Long candidatureId) {
        CandidatureSolo candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        List<Equipe> toutesEquipes = equipeRepository.findByCompetitionId(candidature.getCompetition().getId());
        List<Equipe> equipesCompatibles = new ArrayList<>();

        for (Equipe eq : toutesEquipes) {
            // 1. Vérifier si l'équipe a encore des places libres
            if (eq.getMembres().size() < eq.getCompetition().getTailleEquipeMax()) {

                // Liste des rôles déjà présents dans l'équipe
                Set<RoleEquipe> rolesPresents = new HashSet<>();
                for (MembreEquipe m : eq.getMembres()) {
                    rolesPresents.add(m.getRole());
                }

                // 2. Vérifier si l'équipe manque d'au moins UN des rôles cochés par l'étudiant
                boolean aUnBesoinCompatible = false;
                for (RoleEquipe roleSouhaite : candidature.getRolesSouhaites()) {
                    if (roleSouhaite == RoleEquipe.AUTRE || !rolesPresents.contains(roleSouhaite)) {
                        aUnBesoinCompatible = true;
                        break;
                    }
                }

                if (aUnBesoinCompatible) {
                    equipesCompatibles.add(eq);
                }
            }
        }

        return equipesCompatibles;
    }
}
