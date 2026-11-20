package com.warriors.planning.repositories;

import com.warriors.planning.entities.TableSalle;
import com.warriors.planning.entities.enums.StatutSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableSalleRepository extends JpaRepository<TableSalle, Long> {

    List<TableSalle> findBySalleId(Long salleId);

    List<TableSalle> findBySalleIdAndStatut(Long salleId, StatutSalle statut);

    boolean existsByNumeroAndSalleId(String numero, Long salleId);

    Optional<TableSalle> findByNumeroAndSalleId(String numero, Long salleId);
}
