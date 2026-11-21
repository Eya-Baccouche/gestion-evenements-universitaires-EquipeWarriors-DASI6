package com.esprit.competitions.repository;

import com.esprit.competitions.entity.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {
    Optional<Projet> findByEquipeId(Long equipeId);
    boolean existsByEquipeId(Long equipeId);
}
