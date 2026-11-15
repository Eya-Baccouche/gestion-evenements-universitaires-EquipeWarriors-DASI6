package com.esprit.competitions.repository;

import com.esprit.competitions.entity.MembreEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembreEquipeRepository extends JpaRepository<MembreEquipe, Long> {
    List<MembreEquipe> findByEquipeId(Long equipeId);
    Optional<MembreEquipe> findByEquipeIdAndUserId(Long equipeId, Long userId);
    boolean existsByEquipeCompetitionIdAndUserId(Long competitionId, Long userId);
}
