package com.esprit.competitions.controller;

import com.esprit.competitions.dto.EvaluationDTO;
import com.esprit.competitions.entity.Evaluation;
import com.esprit.competitions.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<Evaluation> evaluerProjet(@Valid @RequestBody EvaluationDTO dto) {
        return new ResponseEntity<>(evaluationService.evaluerProjet(dto), HttpStatus.CREATED);
    }
}
