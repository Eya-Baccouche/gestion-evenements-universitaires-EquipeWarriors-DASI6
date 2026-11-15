package com.esprit.competitions.service;

import com.esprit.competitions.dto.ProjetDTO;
import com.esprit.competitions.entity.Equipe;
import com.esprit.competitions.entity.Projet;
import com.esprit.competitions.repository.EquipeRepository;
import com.esprit.competitions.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final EquipeRepository equipeRepository;

    public Projet getProjetByEquipe(Long equipeId) {
        return projetRepository.findByEquipeId(equipeId)
                .orElseThrow(() -> new RuntimeException("Aucun projet soumis pour cette équipe"));
    }

    @Transactional
    public Projet soumettreProjet(ProjetDTO dto) {
        Equipe equipe = equipeRepository.findById(dto.getEquipeId())
                .orElseThrow(() -> new RuntimeException("Équipe introuvable"));

        if (projetRepository.existsByEquipeId(dto.getEquipeId())) {
            throw new RuntimeException("Un projet a déjà été soumis pour cette équipe");
        }

        Projet projet = Projet.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .depotGithub(dto.getDepotGithub())
                .lienPresentation(dto.getLienPresentation())
                .dateSoumission(LocalDateTime.now())
                .equipe(equipe)
                .build();

        return projetRepository.save(projet);
    }
}
