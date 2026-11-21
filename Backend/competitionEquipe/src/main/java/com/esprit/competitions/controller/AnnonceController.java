package com.esprit.competitions.controller;

import com.esprit.competitions.dto.AnnonceDTO;
import com.esprit.competitions.entity.Annonce;
import com.esprit.competitions.service.AnnonceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class AnnonceController {

    private final AnnonceService annonceService;

    @GetMapping("/{competitionId}/annonces")
    public ResponseEntity<List<Annonce>> getAnnoncesByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(annonceService.getAnnoncesByCompetition(competitionId));
    }

    @PostMapping("/{competitionId}/annonces")
    public ResponseEntity<Annonce> publierAnnonce(
            @PathVariable Long competitionId,
            @Valid @RequestBody AnnonceDTO dto) {
        return new ResponseEntity<>(annonceService.publierAnnonce(competitionId, dto), HttpStatus.CREATED);
    }
}
