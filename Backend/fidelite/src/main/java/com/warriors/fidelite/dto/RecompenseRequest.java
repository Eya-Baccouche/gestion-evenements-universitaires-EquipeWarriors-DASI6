package com.warriors.fidelite.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecompenseRequest(
        @NotBlank String nom,
        @NotNull @Min(1) Integer coutPoints,
        @NotNull @Min(0) Integer stock) {
}
