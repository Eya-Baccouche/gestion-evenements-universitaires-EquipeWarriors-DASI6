package com.warriors.fidelite.controller;

import com.warriors.fidelite.dto.ClassementDto;
import com.warriors.fidelite.model.Compte;
import com.warriors.fidelite.model.Mouvement;
import com.warriors.fidelite.service.FidelityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loyalty")
@RequiredArgsConstructor
@Tag(name = "Compte et classement")
@SecurityRequirement(name = "bearerAuth")
public class LoyaltyController {

    private final FidelityService service;

    @Operation(summary = "Mon compte : points et niveau (200)")
    @GetMapping("/me")
    public Compte monCompte(@AuthenticationPrincipal Jwt jwt) {
        return service.monCompte(jwt.getSubject());
    }

    @Operation(summary = "Mon historique de mouvements, du plus recent au plus ancien (200)")
    @GetMapping("/me/movements")
    public List<Mouvement> monHistorique(@AuthenticationPrincipal Jwt jwt) {
        return service.historique(jwt.getSubject());
    }

    @Operation(summary = "Classement des participants par points (200)")
    @GetMapping("/leaderboard")
    public List<ClassementDto> classement(@RequestParam(defaultValue = "10") int limit) {
        return service.classement(limit);
    }

    @Operation(summary = "Compte d'un participant (200 / 404) - Admin, Organisateur")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANISATEUR')")
    @GetMapping("/accounts/{participantId}")
    public Compte compte(@PathVariable String participantId) {
        return service.compte(participantId);
    }
}
