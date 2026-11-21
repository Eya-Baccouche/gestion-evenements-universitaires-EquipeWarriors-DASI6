package com.esprit.competitions.controller;

import com.esprit.competitions.dto.ProjetDTO;
import com.esprit.competitions.entity.Projet;
import com.esprit.competitions.service.ProjetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;

    @GetMapping("/equipe/{equipeId}")
    public ResponseEntity<Projet> getProjetByEquipe(@PathVariable Long equipeId) {
        return ResponseEntity.ok(projetService.getProjetByEquipe(equipeId));
    }

    @PostMapping
    public ResponseEntity<Projet> soumettreProjet(@Valid @RequestBody ProjetDTO dto) {
        return new ResponseEntity<>(projetService.soumettreProjet(dto), HttpStatus.CREATED);
    }
}
