package com.esprit.competitions.repository;

import com.esprit.competitions.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByCodeInvitation(String codeInvitation);
    List<Equipe> findByCompetitionId(Long competitionId);
    List<Equipe> findByCompetitionIdOrderByScoreFinalDesc(Long competitionId);
    boolean existsByNomAndCompetitionId(String nom, Long competitionId);
}
