package com.esprit.competitions.service;

import com.esprit.competitions.dto.AnnonceDTO;
import com.esprit.competitions.entity.Annonce;
import com.esprit.competitions.entity.Competition;
import com.esprit.competitions.entity.TypeAnnonce;
import com.esprit.competitions.repository.AnnonceRepository;
import com.esprit.competitions.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final CompetitionRepository competitionRepository;

    public List<Annonce> getAnnoncesByCompetition(Long competitionId) {
        return annonceRepository.findByCompetitionIdOrderByDatePublicationDesc(competitionId);
    }

    @Transactional
    public Annonce publierAnnonce(Long competitionId, AnnonceDTO dto) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Compétition introuvable avec l'ID : " + competitionId));

        Annonce annonce = Annonce.builder()
                .titre(dto.getTitre())
                .message(dto.getMessage())
                .type(dto.getType() != null ? dto.getType() : TypeAnnonce.INFO)
                .datePublication(LocalDateTime.now())
                .competition(competition)
                .build();

        log.info("Nouvelle annonce publiée pour la compétition {}: [{}] {}", competitionId, annonce.getType(), annonce.getTitre());
        return annonceRepository.save(annonce);
    }
}
