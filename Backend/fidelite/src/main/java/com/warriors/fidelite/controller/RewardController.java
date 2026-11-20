package com.warriors.fidelite.controller;

import com.warriors.fidelite.dto.EchangeResponse;
import com.warriors.fidelite.dto.RecompenseRequest;
import com.warriors.fidelite.model.Recompense;
import com.warriors.fidelite.service.FidelityService;
import com.warriors.fidelite.service.RecompenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Tag(name = "Recompenses")
@SecurityRequirement(name = "bearerAuth")
public class RewardController {

    private final RecompenseService recompenseService;
    private final FidelityService fidelityService;

    @Operation(summary = "Catalogue des recompenses (200)")
    @GetMapping
    public List<Recompense> catalogue() {
        return recompenseService.lister();
    }

    @Operation(summary = "Creer une recompense (201 / 400 / 403) - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recompense creer(@Valid @RequestBody RecompenseRequest request) {
        return recompenseService.creer(request);
    }

    @Operation(summary = "Modifier nom, cout ou stock (200 / 404) - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Recompense modifier(@PathVariable String id, @Valid @RequestBody RecompenseRequest request) {
        return recompenseService.modifier(id, request);
    }

    @Operation(summary = "Echanger des points contre une recompense (200 / 404 / 409 si points insuffisants ou stock epuise) - Participant")
    @PreAuthorize("hasRole('PARTICIPANT')")
    @PostMapping("/{id}/redeem")
    public EchangeResponse echanger(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return fidelityService.echanger(jwt.getSubject(), id);
    }
}
