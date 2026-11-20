package com.warriors.planning.services;

import com.warriors.planning.dto.TableSalleDTO;
import com.warriors.planning.entities.Salle;
import com.warriors.planning.entities.TableSalle;
import com.warriors.planning.entities.enums.StatutSalle;
import com.warriors.planning.exceptions.BusinessException;
import com.warriors.planning.exceptions.ResourceNotFoundException;
import com.warriors.planning.repositories.ReservationSalleRepository;
import com.warriors.planning.repositories.SalleRepository;
import com.warriors.planning.repositories.TableSalleRepository;
import com.warriors.planning.entities.enums.StatutReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier des tables individuelles (ADMIN).
 * Une table appartient à une salle et peut être réservée individuellement.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TableSalleService {

    private final TableSalleRepository tableSalleRepository;
    private final SalleRepository salleRepository;
    private final ReservationSalleRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<TableSalleDTO> findAll() {
        return tableSalleRepository.findAll().stream()
                .map(TableSalleDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TableSalleDTO> findBySalle(Long salleId) {
        return tableSalleRepository.findBySalleId(salleId).stream()
                .map(TableSalleDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TableSalleDTO findById(Long id) {
        return tableSalleRepository.findById(id)
                .map(TableSalleDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Table introuvable avec l'id " + id));
    }

    public TableSalleDTO create(TableSalleDTO dto) {
        Salle salle = salleRepository.findById(dto.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salle introuvable avec l'id " + dto.getSalleId()));

        if (tableSalleRepository.existsByNumeroAndSalleId(dto.getNumero(), dto.getSalleId())) {
            throw new BusinessException("Une table '" + dto.getNumero()
                    + "' existe déjà dans cette salle");
        }

        TableSalle table = TableSalle.builder()
                .numero(dto.getNumero())
                .capacite(dto.getCapacite())
                .salle(salle)
                .statut(dto.getStatut() != null ? dto.getStatut() : StatutSalle.DISPONIBLE)
                .description(dto.getDescription())
                .build();
        return TableSalleDTO.fromEntity(tableSalleRepository.save(table));
    }

    public TableSalleDTO update(Long id, TableSalleDTO dto) {
        TableSalle table = tableSalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table introuvable avec l'id " + id));

        if (!table.getNumero().equals(dto.getNumero())
                && tableSalleRepository.existsByNumeroAndSalleId(dto.getNumero(), table.getSalle().getId())) {
            throw new BusinessException("Une table '" + dto.getNumero() + "' existe déjà dans cette salle");
        }

        table.setNumero(dto.getNumero());
        table.setCapacite(dto.getCapacite());
        if (dto.getStatut() != null) {
            table.setStatut(dto.getStatut());
        }
        table.setDescription(dto.getDescription());
        return TableSalleDTO.fromEntity(tableSalleRepository.save(table));
    }

    /** R-T-del : ne pas supprimer une table avec des réservations actives */
    public void delete(Long id) {
        TableSalle table = tableSalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table introuvable avec l'id " + id));

        boolean hasReservationsActives = reservationRepository.findAll().stream()
                .filter(r -> r.getTable() != null && r.getTable().getId().equals(id))
                .anyMatch(r -> r.getStatut() == StatutReservation.EN_ATTENTE
                        || r.getStatut() == StatutReservation.CONFIRMEE);

        if (hasReservationsActives) {
            throw new BusinessException(
                    "Impossible de supprimer la table : des réservations actives existent");
        }
        tableSalleRepository.delete(table);
    }
}
