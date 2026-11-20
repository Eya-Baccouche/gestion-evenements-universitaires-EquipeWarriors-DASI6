package com.warriors.planning.controllers;

import com.warriors.planning.dto.TableSalleDTO;
import com.warriors.planning.services.TableSalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API des tables individuelles — réservée au rôle ADMIN.
 * Permet de gérer les tables dans les salles (CRUD).
 */
@RestController
@RequestMapping("/api/planning/tables")
@RequiredArgsConstructor
public class TableSalleController {

    private final TableSalleService tableSalleService;

    @GetMapping
    public ResponseEntity<List<TableSalleDTO>> findAll() {
        return ResponseEntity.ok(tableSalleService.findAll());
    }

    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<TableSalleDTO>> findBySalle(@PathVariable Long salleId) {
        return ResponseEntity.ok(tableSalleService.findBySalle(salleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableSalleDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tableSalleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TableSalleDTO> create(@Valid @RequestBody TableSalleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableSalleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableSalleDTO> update(@PathVariable Long id,
                                                @Valid @RequestBody TableSalleDTO dto) {
        return ResponseEntity.ok(tableSalleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableSalleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
