package com.esprit.competitions.repository;

import com.esprit.competitions.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    List<Annonce> findByCompetitionIdOrderByDatePublicationDesc(Long competitionId);
}
