package com.esprit.competitions.controller;

import com.esprit.competitions.dto.CompetitionDTO;
import com.esprit.competitions.entity.Competition;
import com.esprit.competitions.service.CompetitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public ResponseEntity<List<Competition>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Competition> getCompetitionById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getCompetitionById(id));
    }

    @PostMapping
    public ResponseEntity<Competition> createCompetition(@Valid @RequestBody CompetitionDTO dto) {
        return new ResponseEntity<>(competitionService.createCompetition(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<Competition> changerStatut(
            @PathVariable Long id,
            @RequestParam com.esprit.competitions.entity.StatutCompetition nouveauStatut) {
        return ResponseEntity.ok(competitionService.changerStatut(id, nouveauStatut));
    }

    @PutMapping("/{id}/cloturer")
    public ResponseEntity<Competition> cloturerCompetition(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.cloturerCompetition(id));
    }
}
