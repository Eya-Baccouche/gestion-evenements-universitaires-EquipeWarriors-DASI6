package com.esprit.competitions.repository;

import com.esprit.competitions.entity.CandidatureSolo;
import com.esprit.competitions.entity.StatutMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureSoloRepository extends JpaRepository<CandidatureSolo, Long> {
    List<CandidatureSolo> findByCompetitionIdAndStatut(Long competitionId, StatutMatching statut);
    boolean existsByCompetitionIdAndUserId(Long competitionId, Long userId);
}
