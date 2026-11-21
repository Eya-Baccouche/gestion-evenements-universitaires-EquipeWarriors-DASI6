package com.esprit.competitions.controller;

import com.esprit.competitions.dto.CandidatureSoloDTO;
import com.esprit.competitions.entity.CandidatureSolo;
import com.esprit.competitions.entity.Equipe;
import com.esprit.competitions.service.SmartMatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/matching")
@RequiredArgsConstructor
public class SmartMatchingController {

    private final SmartMatchingService smartMatchingService;

    @PostMapping("/postuler")
    public ResponseEntity<CandidatureSolo> postulerSolo(@Valid @RequestBody CandidatureSoloDTO dto) {
        return new ResponseEntity<>(smartMatchingService.postulerSolo(dto), HttpStatus.CREATED);
    }

    @GetMapping("/suggestions/{candidatureId}")
    public ResponseEntity<List<Equipe>> getSuggestionsEquipes(@PathVariable Long candidatureId) {
        return ResponseEntity.ok(smartMatchingService.trouverEquipesCompatibles(candidatureId));
    }
}
