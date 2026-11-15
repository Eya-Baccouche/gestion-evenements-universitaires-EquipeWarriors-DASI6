package com.esprit.competitions.service;

import com.esprit.competitions.dto.EvaluationDTO;
import com.esprit.competitions.entity.Competition;
import com.esprit.competitions.entity.Equipe;
import com.esprit.competitions.entity.Evaluation;
import com.esprit.competitions.entity.Projet;
import com.esprit.competitions.repository.EquipeRepository;
import com.esprit.competitions.repository.EvaluationRepository;
import com.esprit.competitions.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ProjetRepository projetRepository;
    private final EquipeRepository equipeRepository;

    @Transactional
    public Evaluation evaluerProjet(EvaluationDTO dto) {
        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));

        if (evaluationRepository.existsByProjetIdAndJuryId(dto.getProjetId(), dto.getJuryId())) {
            throw new RuntimeException("Ce jury a déjà évalué ce projet");
        }

        Evaluation evaluation = Evaluation.builder()
                .juryId(dto.getJuryId())
                .noteInnovation(dto.getNoteInnovation())
                .noteTechnique(dto.getNoteTechnique())
                .notePitch(dto.getNotePitch())
                .feedback(dto.getFeedback())
                .dateEvaluation(LocalDateTime.now())
                .projet(projet)
                .build();

        // Récupérer les coefficients personnalisés de la compétition
        Competition competition = projet.getEquipe().getCompetition();
        evaluation.calculerNoteGlobale(
                competition.getCoefInnovation(),
                competition.getCoefTechnique(),
                competition.getCoefPitch()
        );

        evaluation = evaluationRepository.save(evaluation);

        // Mettre à jour le score moyen de l'équipe
        List<Evaluation> toutesEvaluations = evaluationRepository.findByProjetId(projet.getId());
        double moyenneScore = toutesEvaluations.stream()
                .mapToDouble(Evaluation::getNoteGlobale)
                .average()
                .orElse(0.0);

        Equipe equipe = projet.getEquipe();
        equipe.setScoreFinal(Math.round(moyenneScore * 100.0) / 100.0);
        equipeRepository.save(equipe);

        return evaluation;
    }
}
