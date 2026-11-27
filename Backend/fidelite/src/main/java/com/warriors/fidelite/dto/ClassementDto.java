package com.warriors.fidelite.dto;

import com.warriors.fidelite.model.NiveauFidelite;

public record ClassementDto(int rang, String participantId, int points, NiveauFidelite niveau) {
}
