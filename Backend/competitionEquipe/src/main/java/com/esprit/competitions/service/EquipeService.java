package com.esprit.competitions.service;

import com.esprit.competitions.dto.EquipeDTO;
import com.esprit.competitions.entity.*;
import com.esprit.competitions.repository.CompetitionRepository;
import com.esprit.competitions.repository.EquipeRepository;
import com.esprit.competitions.repository.MembreEquipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final CompetitionRepository competitionRepository;
    private final MembreEquipeRepository membreEquipeRepository;

    public List<Equipe> getEquipesByCompetition(Long competitionId) {
        return equipeRepository.findByCompetitionId(competitionId);
    }

    public Equipe getEquipeById(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipe introuvable avec l'ID : " + id));
    }

    @Transactional
    public Equipe creerEquipe(Long competitionId, EquipeDTO dto) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Compétition introuvable avec l'ID : " + competitionId));

        if (equipeRepository.existsByNomAndCompetitionId(dto.getNom(), competitionId)) {
            throw new RuntimeException("Une équipe avec ce nom existe déjà pour cette compétition");
        }

        if (membreEquipeRepository.existsByEquipeCompetitionIdAndUserId(competitionId, dto.getLeaderId())) {
            throw new RuntimeException("L'étudiant est déjà inscrit dans une équipe pour cette compétition");
        }

        // Création de l'équipe avec un code d'invitation unique
        Equipe equipe = Equipe.builder()
                .nom(dto.getNom())
                .codeInvitation(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .leaderId(dto.getLeaderId())
                .competition(competition)
                .build();

        equipe = equipeRepository.save(equipe);

        // Ajouter le leader comme premier membre
        MembreEquipe leader = MembreEquipe.builder()
                .userId(dto.getLeaderId())
                .role(RoleEquipe.LEADER)
                .equipe(equipe)
                .dateAdhesion(LocalDateTime.now())
                .build();

        membreEquipeRepository.save(leader);

        return equipe;
    }

    @Transactional
    public Equipe rejoindreEquipeParCode(String codeInvitation, Long userId, RoleEquipe role) {
        Equipe equipe = equipeRepository.findByCodeInvitation(codeInvitation)
                .orElseThrow(() -> new RuntimeException("Code d'invitation invalide"));

        Long competitionId = equipe.getCompetition().getId();

        if (membreEquipeRepository.existsByEquipeCompetitionIdAndUserId(competitionId, userId)) {
            throw new RuntimeException("Vous êtes déjà membre d'une équipe pour cette compétition");
        }

        if (equipe.getMembres().size() >= equipe.getCompetition().getTailleEquipeMax()) {
            throw new RuntimeException("L'équipe a atteint sa taille maximale (" + equipe.getCompetition().getTailleEquipeMax() + " membres)");
        }

        MembreEquipe membre = MembreEquipe.builder()
                .userId(userId)
                .role(role != null ? role : RoleEquipe.DEVELOPPEUR_FULLSTACK)
                .equipe(equipe)
                .dateAdhesion(LocalDateTime.now())
                .build();

        membreEquipeRepository.save(membre);
        return equipeRepository.findById(equipe.getId()).orElse(equipe);
    }

    @Transactional
    public Equipe affecterTable(Long equipeId, Long tableId) {
        Equipe equipe = getEquipeById(equipeId);
        equipe.setTableId(tableId);
        return equipeRepository.save(equipe);
    }
}
