package com.esprit.competitions.repository;

import com.esprit.competitions.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByProjetId(Long projetId);
    boolean existsByProjetIdAndJuryId(Long projetId, Long juryId);
}
