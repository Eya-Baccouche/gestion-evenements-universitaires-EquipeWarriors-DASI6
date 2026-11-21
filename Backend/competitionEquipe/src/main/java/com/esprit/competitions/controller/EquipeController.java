package com.esprit.competitions.controller;

import com.esprit.competitions.dto.EquipeDTO;
import com.esprit.competitions.entity.Equipe;
import com.esprit.competitions.entity.RoleEquipe;
import com.esprit.competitions.service.EquipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;

    @GetMapping("/{competitionId}/equipes")
    public ResponseEntity<List<Equipe>> getEquipesByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(equipeService.getEquipesByCompetition(competitionId));
    }

    @GetMapping("/equipes/{equipeId}")
    public ResponseEntity<Equipe> getEquipeById(@PathVariable Long equipeId) {
        return ResponseEntity.ok(equipeService.getEquipeById(equipeId));
    }

    @PostMapping("/{competitionId}/equipes")
    public ResponseEntity<Equipe> creerEquipe(
            @PathVariable Long competitionId,
            @Valid @RequestBody EquipeDTO dto) {
        return new ResponseEntity<>(equipeService.creerEquipe(competitionId, dto), HttpStatus.CREATED);
    }

    @PostMapping("/equipes/rejoindre")
    public ResponseEntity<Equipe> rejoindreParCode(
            @RequestParam String codeInvitation,
            @RequestParam Long userId,
            @RequestParam(required = false) RoleEquipe role) {
        return ResponseEntity.ok(equipeService.rejoindreEquipeParCode(codeInvitation, userId, role));
    }

    @PutMapping("/equipes/{equipeId}/table")
    public ResponseEntity<Equipe> affecterTable(
            @PathVariable Long equipeId,
            @RequestParam Long tableId) {
        return ResponseEntity.ok(equipeService.affecterTable(equipeId, tableId));
    }
}
