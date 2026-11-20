package com.warriors.fidelite.dto;

import com.warriors.fidelite.model.NiveauFidelite;

public record EchangeResponse(String recompense, int pointsDepenses, int soldeRestant, NiveauFidelite niveau) {
}
